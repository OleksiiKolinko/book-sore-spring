package mate.academy.service;

import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String userEmail, Pageable pageable);

    CartItemDto addCartItem(CreateCartItemDto cartItemDto, String userEmail);

    CartItemDto updateQuantity(Long cartItemId, QuantityCartItemDto cartItemDto,
                               String userEmail);

    void deleteCartItemId(Long cartItemId, String userEmail);
}
