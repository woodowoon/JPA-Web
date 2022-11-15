package com.example.jpaweb.api;

import com.example.jpaweb.domain.Address;
import com.example.jpaweb.domain.Order;
import com.example.jpaweb.domain.OrderItem;
import com.example.jpaweb.domain.OrderStatus;
import com.example.jpaweb.repository.OrderRepository;
import com.example.jpaweb.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // V1 : 엔티티 직접 노출
    // @GetMapping("/api/v1/orders")
    // 이게 안되는 이유는 제이슨이그노어를 해줬어야 했는데, 그 작업을 안해줘서 인 것 같다.
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems(); // 강제 초기화
            orderItems.stream().forEach(o -> o.getItem().getName()); // 강제 초기화
        }
        return all;
    }

    // V2 : 엔티티를 DTO로 변환
    // 근데 성능상 많은 쿼리가 나가기 때문에 좋지 않다. 따라서, 패치 조인을 활용해서 성능 최적화를 해보려고 한다.
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    // 패치조인은 페이징 처리가 불가능하다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orersV2() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName; // 상품 명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
