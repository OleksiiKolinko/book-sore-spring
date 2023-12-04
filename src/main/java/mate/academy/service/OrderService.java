package mate.academy.service;

import java.util.Set;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.dto.order.OrderRequestDto;
import mate.academy.dto.order.OrderStatusDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(Long userId, OrderRequestDto shippingAddress);

    Set<OrderDto> getHistory(Long userId, Pageable pageable);

    Set<OrderItemDto> getAllOrderItems(Long orderId, Long userId);

    OrderItemDto getOrderItem(Long orderId, Long itemId, Long userId);

    OrderDto updateOrderStatus(Long orderId, OrderStatusDto statusDto);
}
