package mate.academy.service.impl;

import jakarta.transaction.Transactional;
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
    public ShoppingCartDto getShoppingCart(Long userId, Pageable pageable) {
        return shoppingCartMapper.toDto(shoppingCartRepository
                .findShoppingCartById(userId));
    }

    @Override
    @Transactional
    public CartItemDto addCartItem(CreateCartItemDto cartItemDto, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartById(userId);
        CartItem cartItem = cartItemMapper.toModel(cartItemDto);
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
                                      Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartById(userId);
        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Can't find item by id "
                                + cartItemId + " for user with id " + userId));
        cartItem.setQuantity(cartItemDto.quantity());
        shoppingCartRepository.save(shoppingCart);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteCartItemId(Long cartItemId, Long userEmail) {
        if (getMatch(cartItemId, userEmail)) {
            CartItem cartItem = Optional.of(cartItemRepository.findById(cartItemId))
                    .get().orElseThrow(
                            () -> new EntityNotFoundException(
                                    "Can't find cart item by id: " + cartItemId)
            );
            cartItemRepository.delete(cartItem);
        }
    }

    private Long getUserId(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new RegistrationException(
                "Users id with this email: " + userEmail + "is not exist")).getId();
    }

    private boolean getMatch(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findShoppingCartById(userId);
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        return cartItems.stream().anyMatch(c -> c.getId().equals(cartItemId));
    }
}
