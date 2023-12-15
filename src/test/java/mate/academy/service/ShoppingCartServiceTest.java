package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.Category;
import mate.academy.model.Role;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.shoppingcart.CartItemRepository;
import mate.academy.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.repository.user.UserRepository;
import mate.academy.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    private static final Long ID_ONE = 1L;
    private static final int QUANTITY = 5;
    private static final String TITLE = "Title1";
    private static final Long ID_TWO = 2L;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Verify getShoppingCart() method works")
    public void getShoppingCart_Valid_ReturnShoppingCartDto() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(ID_ONE, ID_TWO, getCartItemDtos());
        ShoppingCart shoppingCart = getShoppingCart();
        Pageable pageable = PageRequest.of(0, 10);
        when(shoppingCartRepository.findShoppingCartById(anyLong())).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(shoppingCartDto);
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(ID_TWO, pageable);
        assertEquals(shoppingCartDto, actual);
        verify(shoppingCartRepository, times(1)).findShoppingCartById(anyLong());
        verify(shoppingCartMapper, times(1)).toDto(any(ShoppingCart.class));
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify addCartItem() method works")
    public void addCartItem_Valid_ReturnCartItemDto() {
        User user = getUser();
        CartItemDto cartItemDto = new CartItemDto(ID_ONE, ID_ONE, TITLE, QUANTITY);
        CartItem cartItem = getCartItem();
        CreateCartItemDto createCartItemDto = new CreateCartItemDto(ID_ONE, QUANTITY);
        Book book = getBook();
        when(cartItemMapper.toModel(any(CreateCartItemDto.class))).thenReturn(cartItem);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDto(any(CartItem.class))).thenReturn(cartItemDto);
        CartItemDto actual = shoppingCartService.addCartItem(createCartItemDto, user.getId());
        assertEquals(cartItemDto, actual);
        verify(cartItemMapper, times(1)).toModel(any(CreateCartItemDto.class));
        verify(cartItemMapper, times(1)).toDto(any(CartItem.class));
        verify(bookRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verifyNoMoreInteractions(cartItemMapper, bookRepository,
                userRepository, cartItemRepository);
    }

    @Test
    @DisplayName("Verify updateQuantity() method works")
    public void updateQuantity_Valid_CartItemDto() {
        QuantityCartItemDto quantityCartItemDto = new QuantityCartItemDto(QUANTITY);
        CartItemDto cartItemDto = new CartItemDto(ID_ONE, ID_ONE, TITLE, QUANTITY);
        CartItem cartItem = getCartItem();
        ShoppingCart shoppingCart = getShoppingCart();
        when(shoppingCartRepository.findShoppingCartById(anyLong())).thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(anyLong(),
                anyLong())).thenReturn(Optional.of(cartItem));
        when(cartItemMapper.toDto(cartItemRepository.save(cartItem))).thenReturn(cartItemDto);
        CartItemDto actual = shoppingCartService
                .updateQuantity(ID_ONE, quantityCartItemDto, ID_TWO);
        assertEquals(cartItemDto, actual);
        verify(shoppingCartRepository, times(1)).findShoppingCartById(anyLong());
        verify(cartItemRepository, times(1)).findByIdAndShoppingCartId(anyLong(), anyLong());
        verify(cartItemMapper, times(1)).toDto(cartItemRepository.save(cartItem));
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Verify getShoppingCart() method works")
    public void addCartItem_WithNonExistingBookId_ShouldThrowException() {
        CreateCartItemDto createCartItemDto = new CreateCartItemDto(ID_TWO, QUANTITY);
        when(bookRepository.findById(ID_TWO)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(createCartItemDto, ID_TWO));
        String expected = "Can't find book with id: " + ID_TWO;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(ID_TWO);
        verifyNoMoreInteractions(bookRepository);
    }

    private CartItem getCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(ID_ONE);
        cartItem.setBook(getBook());
        cartItem.setQuantity(QUANTITY);
        return cartItem;
    }

    private ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(getCartItem());
        shoppingCart.setCartItems(cartItems);
        shoppingCart.setUser(getUser());
        shoppingCart.setId(ID_ONE);
        return shoppingCart;
    }

    private Book getBook() {
        Book book = new Book();
        book.setId(ID_ONE);
        book.setTitle(TITLE);
        book.setAuthor("Author1");
        book.setIsbn("Isbn1");
        book.setPrice(BigDecimal.valueOf(20));
        book.setDescription("Description1");
        book.setCoverImage("Cover Image1");
        book.setCategories(getCategory());
        return book;
    }

    private Set<Category> getCategory() {
        final Set<Category> categories = new HashSet<>();
        Category category = new Category();
        category.setId(ID_ONE);
        category.setName("Fiction");
        category.setDescription("Fiction books");
        categories.add(category);
        return categories;
    }

    private User getUser() {
        User user = new User();
        user.setRoles(getRoleUser());
        user.setId(ID_TWO);
        user.setEmail("Jon.doe@example.com");
        user.setPassword("password1");
        user.setFirstName("firstName1");
        user.setLastName("lastName1");
        user.setShippingAddress("shippingAddress1");
        return user;
    }

    private Set<Role> getRoleUser() {
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(ID_TWO);
        role.setName(Role.RoleName.ROLE_USER);
        roles.add(role);
        return roles;
    }

    private Set<CartItemDto> getCartItemDtos() {
        CartItemDto cartItemDto = new CartItemDto(ID_ONE, ID_ONE, TITLE, QUANTITY);
        Set<CartItemDto> cartItemDtos = new HashSet<>();
        cartItemDtos.add(cartItemDto);
        return cartItemDtos;
    }
}
