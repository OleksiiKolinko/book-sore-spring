package mate.academy.service.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.dto.order.OrderRequestDto;
import mate.academy.dto.order.OrderStatusDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.OrderItemMapper;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.CartItem;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.order.OrderItemRepository;
import mate.academy.repository.order.OrderRepository;
import mate.academy.repository.shoppingcart.CartItemRepository;
import mate.academy.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.repository.user.UserRepository;
import mate.academy.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, OrderRequestDto shippingAddress) {
        Order newOrder = orderMapper.toModel(shippingAddress);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RegistrationException("Can't find user by id: " + userId));
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(Order.Status.PENDING);
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartById(userId);
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        BigDecimal total = cartItems.stream()
                .map(o -> o.getBook().getPrice().multiply(BigDecimal.valueOf(o.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setTotal(total);
        orderRepository.save(newOrder);
        Set<OrderItem> orderItems = getOrderItemsSet(cartItems, newOrder);
        newOrder.setOrderItems(orderItems);
        return orderMapper.toDto(newOrder);
    }

    private Set<OrderItem> getOrderItemsSet(Set<CartItem> cartItems, Order newOrder) {
        return cartItems.stream()
                .map(o -> saveOrderItem(o, newOrder))
                .collect(Collectors.toSet());
    }

    private OrderItem saveOrderItem(CartItem cartItem, Order newOrder) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(newOrder);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setBook(cartItem.getBook());
        orderItem.setPrice(cartItem.getBook().getPrice());
        cartItemRepository.delete(cartItem);
        return orderItemRepository.save(orderItem);
    }

    @Override
    public Set<OrderDto> getHistory(Long userId, Pageable pageable) {
        Set<Order> orders = orderRepository.findAllByUserId(userId, pageable).toSet();
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<OrderItemDto> getAllOrderItems(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order by id: " + orderId));
        Set<OrderItem> orderItems = order.getOrderItems();
        return orderItems.stream().map(orderItemMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId, Long userId) {
        OrderItem orderItem = orderItemRepository
                .findByIdAndOrderIdAndUserId(itemId, orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order item "
                        + "by order id: " + orderId + " and item id: " + itemId
                        + " and user id: " + userId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatusDto statusDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order by id: " + orderId));
        order.setStatus(Order.Status.valueOf(statusDto.status()));
        return orderMapper.toDto(orderRepository.save(order));
    }
}
