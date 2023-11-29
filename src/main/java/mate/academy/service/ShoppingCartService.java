package mate.academy.service;

import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(Authentication authentication, Pageable pageable);

    CartItemDto addCartItem(CreateCartItemDto cartItemDto, Authentication authentication);

    CartItemDto updateQuantity(Long cartItemId, QuantityCartItemDto cartItemDto,
                               Authentication authentication);

    void deleteCartItemId(Long cartItemId, Authentication authentication);
}
