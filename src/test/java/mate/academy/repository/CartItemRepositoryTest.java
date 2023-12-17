package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.CartItem;
import mate.academy.repository.shoppingcart.CartItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    private static final Long ID_ONE = 1L;
    private static final String DATA_SOURCE_REMOVE =
            "classpath:database/cart_items/remove-data-after-testing-cart-item-repository.sql";
    private static final String DATA_SOURCE_ADD_DATA =
            "classpath:database/cart_items/add-data-to-check-cart-item-repository.sql";
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Verify findByIdAndShoppingCartId() method works")
    @Sql(scripts =
            DATA_SOURCE_REMOVE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DATA_SOURCE_ADD_DATA,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            DATA_SOURCE_REMOVE,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndShoppingCartId_ValidCartItemAndShoppingCard_returnCartItem() {
        Optional<CartItem> actual = cartItemRepository.findByIdAndShoppingCartId(ID_ONE, ID_ONE);
        Optional<CartItem> expected = cartItemRepository.findById(ID_ONE);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
