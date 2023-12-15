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
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Verify findShoppingCartById() method works")
    @Sql(scripts =
            "classpath:database/cart_items/remove-data-after-testing-cart-item-repository.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cart_items/add-data-to-check-cart-item-repository.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/cart_items/remove-data-after-testing-cart-item-repository.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findShoppingCartById_ValidShoppingCartAndUser_returnShoppingCart() {
        ShoppingCart actual = shoppingCartRepository.findShoppingCartById(ID_ONE);
        ShoppingCart expected = shoppingCartRepository.findById(ID_ONE).orElseThrow();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
