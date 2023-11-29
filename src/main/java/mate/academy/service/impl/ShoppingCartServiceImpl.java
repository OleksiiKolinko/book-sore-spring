package mate.academy.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.shoppingcart.CartItemRepository;
import mate.academy.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.repository.user.UserRepository;
import mate.academy.service.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getShoppingCart(Authentication authentication, Pageable pageable) {
        return shoppingCartMapper.toDto(shoppingCartRepository
                .findUserById(getUserId(authentication)));
    }

    @Override
    public CartItemDto addCartItem(CreateCartItemDto cartItemDto, Authentication authentication) {
        Long userId = getUserId(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findUserById(userId);
        CartItem cartItem = cartItemMapper.toModel(cartItemDto);
        if (shoppingCart == null) {
            Set<CartItem> cartItemSet = new HashSet<>();
            cartItemSet.add(cartItem);
            shoppingCart = new ShoppingCart();
            shoppingCart.setUser(userRepository.findById(userId).orElseThrow(
                    () -> new RegistrationException("User  with this id: "
                            + userId + "is not exist")));
            shoppingCart.setCartItems(cartItemSet);
        } else {
            shoppingCart.getCartItems().add(cartItem);
        }
        Long bookId = cartItemDto.bookId();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id: " + bookId)
        );
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        shoppingCartRepository.save(shoppingCart);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto updateQuantity(Long cartItemId, QuantityCartItemDto cartItemDto,
                                      Authentication authentication) {
        if (getMatch(cartItemId, authentication)) {
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find cart item with id: "
                            + cartItemId));
            ShoppingCart shoppingCart = shoppingCartRepository
                    .findUserById(getUserId(authentication));
            Set<CartItem> cartItems = shoppingCart.getCartItems();
            cartItem.setQuantity(cartItemDto.quantity());
            cartItems.add(cartItem);
            shoppingCart.setCartItems(cartItems);
            shoppingCartRepository.save(shoppingCart);
            return cartItemMapper.toDto(cartItemRepository.save(cartItem));
        }
        return null;
    }

    @Override
    public void deleteCartItemId(Long cartItemId, Authentication authentication) {
        if (getMatch(cartItemId, authentication)) {
            CartItem cartItem = Optional.of(cartItemRepository.findById(cartItemId))
                    .get().orElseThrow(
                            () -> new EntityNotFoundException(
                                    "Can't find cart item by id: " + cartItemId)
            );
            cartItemRepository.delete(cartItem);
        }
    }

    private Long getUserId(Authentication authentication) {
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new RegistrationException(
                "Users id with this email: " + userEmail + "is not exist")).getId();
    }

    private boolean getMatch(Long cartItemId, Authentication authentication) {
        ShoppingCart shoppingCart = shoppingCartRepository.findUserById(getUserId(authentication));
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        return cartItems.stream().anyMatch(c -> c.getId().equals(cartItemId));
    }
}
