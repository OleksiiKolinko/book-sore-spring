package mate.academy.repository;

import mate.academy.model.ShoppingCart;
import mate.academy.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    private static final String DATA_SOURCE_REMOVE =
            "classpath:database/cart_items/remove-data-after-testing-cart-item-repository.sql";
    private static final String DATA_SOURCE_ADD_DATA =
            "classpath:database/cart_items/add-data-to-check-cart-item-repository.sql";
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Verify findShoppingCartById() method works")
    @Sql(scripts =
            DATA_SOURCE_REMOVE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DATA_SOURCE_ADD_DATA,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            DATA_SOURCE_REMOVE,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findShoppingCartById_ValidShoppingCartAndUser_returnShoppingCart() {
        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUserId(ID_TWO);
        ShoppingCart expected = shoppingCartRepository.findById(ID_ONE).orElseThrow();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
