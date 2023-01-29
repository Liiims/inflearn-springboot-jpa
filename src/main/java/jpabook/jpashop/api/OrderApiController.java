package jpabook.jpashop.api;

import jpabook.jpashop.domain.member.Address;
import jpabook.jpashop.domain.order.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * v2. 엔티티를 DTO로 변환 <br><br>
     *
     * - 지연로딩으로 매우 많은 SQL 실행 <br>
     *  - order 1번, member N번, address N번, orderItem N번, item N번
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDTO> ordersV2() {
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderDTO::new)
                .collect(toList());
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
