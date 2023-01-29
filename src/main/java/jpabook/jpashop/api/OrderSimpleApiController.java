package jpabook.jpashop.api;

import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderRepository;
import jpabook.jpashop.domain.order.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * v1. 엔티티 직접 노출 <br><br>
     * - Hibernate5JakartaModule 모듈 등록, LAZY = null 처리 <br>
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
//    @GetMapping("/api/v1/simple-orders")
//    public List<Order> ordersV1() {
//        return orderRepository.findAllByString(new OrderSearch());
//    }
}
