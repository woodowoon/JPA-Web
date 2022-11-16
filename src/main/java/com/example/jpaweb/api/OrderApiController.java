package com.example.jpaweb.api;

import com.example.jpaweb.domain.Address;
import com.example.jpaweb.domain.Order;
import com.example.jpaweb.domain.OrderItem;
import com.example.jpaweb.domain.OrderStatus;
import com.example.jpaweb.repository.OrderRepository;
import com.example.jpaweb.repository.OrderSearch;
import com.example.jpaweb.repository.order.query.OrderFlatDto;
import com.example.jpaweb.repository.order.query.OrderItemQueryDto;
import com.example.jpaweb.repository.order.query.OrderQueryDto;
import com.example.jpaweb.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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
                .collect(toList());

        return collect;
    }

    // 패치조인은 페이징 처리가 불가능하다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orersV2() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v3.1/orders") // default_batch_fetch_size 를 이용해서 in 쿼리를 쏴서 최적화를 해준다.
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() { // orderFlat -> OrderQueryDto 변경해준다.
        // return orderQueryRepository.findAllByDto_flat(); // 이거는 order 이 중복적으로 2개씩 나오게 된다.
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream() // 메모리가 다 처리해준다.
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),

                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
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
                    .collect(toList());
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
