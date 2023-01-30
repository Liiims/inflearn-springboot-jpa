package jpabook.jpashop.api;

import jpabook.jpashop.domain.member.Address;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.dto.OrderSimpleQueryDTO;
import jpabook.jpashop.repository.order.query.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화 <br><br>
 *
 * Order <br>
 * Order -> Member <br>
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * v1. 엔티티 직접 노출 <br><br>
     * - Hibernate5JakartaModule 모듈 등록, LAZY = null 처리 <br>
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
//    @GetMapping("/api/v1/simple-orders")
//    public List<Order> ordersV1() {
//        return orderRepository.findAllByString(new OrderSearch());
//    }

    /**
     * v2. 엔티티를 조회해서 DTO 로 변환 (fetch join 사용X) <br><br>
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDTO> ordersV2() {
        // ORDER N개
        // N + 1 문제 발생 -> 1 + N(회원) + N(배송)
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        return orders.stream()
                .map(SimpleOrderDTO::new)
                .collect(toList());
    }

    /**
     * v3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O) <br><br>
     * - fetch join으로 쿼리 1번 호출
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDTO> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        return orders.stream()
                .map(SimpleOrderDTO::new)
                .collect(toList());
    }

    /**
     * v4. JPA에서 DTO로 바로 조회 <br><br>
     * - 쿼리1번 호출 <br>
     * - select 절에서 원하는 데이터만 선택해서 조회
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDTO> ordersV4() {
        return orderSimpleQueryRepository.findOrderDTOs();
    }

    @Data
    static class SimpleOrderDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDTO(Order o) {
            orderId = o.getId();
            name = o.getMember().getName(); // LAZY 초기화
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
