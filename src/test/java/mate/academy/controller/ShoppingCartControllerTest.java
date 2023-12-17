package mate.academy.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.config.SpringSecurityWebAuxTestConfig;
import mate.academy.dto.cartitem.CartItemDto;
import mate.academy.dto.cartitem.CreateCartItemDto;
import mate.academy.dto.cartitem.QuantityCartItemDto;
import mate.academy.dto.shoppingcart.ShoppingCartDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class
)
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    private static final String TITLE = "Title1";
    private static final int QUANTITY_ONE = 1;
    private static final int QUANTITY_FIVE = 5;
    private static final String URL_CART = "/cart";
    private static final String URL_CART_UPDATE_QUANTITY = "/cart/cart-items/1";
    private static final String DATA_SOURCE_ADD_DATA =
            "database/cart_items/add-data-to-check-cart-item-repository.sql";
    private static final String DATA_SOURCE_REMOVE_DATA =
            "database/cart_items/remove-data-after-testing-cart-item-repository.sql";
    private static final String DATA_SOURCE_REMOVE_NEW_CART_ITEM =
            "classpath:database/cart_items/remove-new-cart-item-repository.sql";
    private static final String USER_EMAIL = "Jon1.doe@example.com";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DATA_SOURCE_ADD_DATA));
        }
    }

    @Test
    @SneakyThrows
    @WithUserDetails(USER_EMAIL)
    @DisplayName("Verify getShoppingCart() method works")
    public void getShoppingCart_Valid_ReturnShoppingCartDto() {
        ShoppingCartDto expected = new ShoppingCartDto(ID_ONE, ID_TWO,
                Set.of(getCartItemDtos().get(QUANTITY_ONE)));
        MvcResult result = mockMvc.perform(get(URL_CART).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    @WithUserDetails(USER_EMAIL)
    @DisplayName("Verify addCartItem() method works")
    @Sql(scripts = DATA_SOURCE_REMOVE_NEW_CART_ITEM,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addCartItem_Valid_ReturnCartItemDto() {
        CartItemDto expected = getCartItemDtos().get(QUANTITY_FIVE);
        CreateCartItemDto createCartItemDto = new CreateCartItemDto(ID_ONE, QUANTITY_FIVE);
        String content = objectMapper.writeValueAsString(createCartItemDto);
        MvcResult result = mockMvc.perform(post(URL_CART)
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartItemDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CartItemDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    @WithUserDetails(USER_EMAIL)
    @DisplayName("Verify updateQuantity() method works")
    public void updateQuantity_Valid_ReturnCartItemDto() {
        CartItemDto expectedFirst = new CartItemDto(ID_ONE, ID_ONE, TITLE, QUANTITY_FIVE);
        CartItemDto expectedSecond = getCartItemDtos().get(QUANTITY_ONE);
        QuantityCartItemDto quantityCartItemDtoFirst = new QuantityCartItemDto(QUANTITY_FIVE);
        QuantityCartItemDto quantityCartItemDtoSecond = new QuantityCartItemDto(QUANTITY_ONE);
        String contentFirst = objectMapper.writeValueAsString(quantityCartItemDtoFirst);
        String contentSecond = objectMapper.writeValueAsString((quantityCartItemDtoSecond));
        MvcResult resultFirst = mockMvc.perform(put(URL_CART_UPDATE_QUANTITY).content(contentFirst)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartItemDto actualFirst = objectMapper.readValue(resultFirst.getResponse()
                .getContentAsString(), CartItemDto.class);
        Assertions.assertNotNull(actualFirst);
        Assertions.assertEquals(expectedFirst, actualFirst);
        MvcResult resultSecond = mockMvc.perform(put(URL_CART_UPDATE_QUANTITY)
                        .content(contentSecond).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartItemDto actualSecond = objectMapper.readValue(resultSecond.getResponse()
                .getContentAsString(), CartItemDto.class);
        Assertions.assertNotNull(actualSecond);
        Assertions.assertEquals(expectedSecond, actualSecond);
    }

    private HashMap<Integer, CartItemDto> getCartItemDtos() {
        CartItemDto cartItemDto = new CartItemDto(ID_ONE, ID_ONE, TITLE, QUANTITY_ONE);
        CartItemDto cartItemDto2 = new CartItemDto(ID_TWO, ID_ONE, TITLE, QUANTITY_FIVE);
        HashMap<Integer, CartItemDto> cartItemDtos = new HashMap<>();
        cartItemDtos.put(QUANTITY_ONE, cartItemDto);
        cartItemDtos.put(QUANTITY_FIVE, cartItemDto2);
        return cartItemDtos;
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DATA_SOURCE_REMOVE_DATA));
        }
    }
}
