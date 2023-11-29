package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.service.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for mapping shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart",
            description = "Get all information of  user shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication, Pageable pageable) {
        return shoppingCartService.getShoppingCart(authentication, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Add book to the shopping cart",
            description = "Add book to the shopping cart")
    public CartItemDto addCartItem(@RequestBody @Valid CreateCartItemDto cartItemDto,
                                   Authentication authentication) {

        return shoppingCartService.addCartItem(cartItemDto, authentication);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update quantity of a book in the shopping cart",
            description = "Update quantity of a book in the shopping cart")
    public CartItemDto updateQuantity(@PathVariable Long cartItemId,
                               @RequestBody @Valid QuantityCartItemDto cartItemDto,
                                      Authentication authentication) {
        return shoppingCartService.updateQuantity(cartItemId, cartItemDto, authentication);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Remove a book from the shopping cart",
            description = "Remove a book from the shopping cart by id")
    public void deleteCartItemId(@PathVariable Long cartItemId, Authentication authentication) {

        shoppingCartService.deleteCartItemId(cartItemId, authentication);
    }
}
