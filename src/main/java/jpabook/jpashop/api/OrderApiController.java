package jpabook.jpashop.api;

import jpabook.jpashop.domain.member.Address;
import jpabook.jpashop.domain.order.*;
import jpabook.jpashop.repository.order.query.OrderQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.repository.order.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * v2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X) <br><br>
     *
     * - 트랜잭션 안의 지연로딩으로 매우 많은 SQL 실행 <br>
     *  - order 1번, member N번, address N번, orderItem N번, item N번
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDTO> ordersV2() {
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderDTO::new)
                .collect(toList());
    }

    /**
     * v3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O) <br><br>
     *
     * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDTO> ordersV3() {
        return orderRepository.findAllWithItem().stream()
                .map(OrderDTO::new)
                .collect(toList());
    }

    /**
     * v3.1. 엔티티를 조회해서 DTO로 변환 페이징 고려 <br><br>
     *
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화 <br>
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDTO> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {

        return orderRepository.findAllWithMemberDelivery(offset, limit).stream()
                .map(OrderDTO::new)
                .collect(toList());
    }

    /**
     * v4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1+NQuery) <br><br>
     *
     * - 페이징 가능
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDTO> ordersV4() {
        return orderQueryRepository.findOrderQueryDTOs();
    }

    @Data
    static class OrderDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems; // DTO에 엔티티가 존재하면 안됨, OrderItem도 DTO로 변환하여 반환

        public OrderDTO(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(toList()); // LAZY 초기화
        }
    }

    @Data
    static class OrderItemDTO {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDTO(OrderItem orderItem) {
            itemName = orderItem.getItem().getName(); // LAZY 초기화
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
