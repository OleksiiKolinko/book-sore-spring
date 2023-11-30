package mate.academy.service;

import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(Long userId, Pageable pageable);

    CartItemDto addCartItem(CreateCartItemDto cartItemDto, Long userId);

    CartItemDto updateQuantity(Long cartItemId, QuantityCartItemDto cartItemDto,
                               Long userId);

    void deleteCartItemId(Long cartItemId, Long userId);
}
