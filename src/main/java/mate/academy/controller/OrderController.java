package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.DoOrderDto;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.dto.order.OrderStatusDto;
import mate.academy.model.User;
import mate.academy.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for mapping order")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place an order by shipping address")
    public OrderDto createOrder(Authentication authentication,
                                @RequestBody @Valid DoOrderDto shippingAddress) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(user.getId(), shippingAddress);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Retrieve user's order history",
            description = "Retrieve user's order history")
    public Set<OrderDto> getHistory(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getHistory(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Retrieve all OrderItems for a specific order",
            description = "Retrieve all OrderItems for a specific order")
    public Set<OrderItemDto> getAllOrderItems(@PathVariable Long orderId,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrderItems(orderId, user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Retrieve a specific OrderItem within an order",
            description = "Retrieve a specific OrderItem within an order")
    public OrderItemDto getOrderItem(@PathVariable Long orderId,
                                     @PathVariable Long itemId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItem(orderId, itemId, user.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order status",
            description = "Update order status")
    public OrderDto updateOrderStatus(@PathVariable Long orderId,
                                      @RequestBody @Valid OrderStatusDto statusDto) {
        return orderService.updateOrderStatus(orderId, statusDto);
    }
}
