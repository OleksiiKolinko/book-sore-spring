package mate.academy.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.dto.category.CategoryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    private static MockMvc mockMvc;
    private static final CategoryDto CATEGORY_DTO_1 =
            new CategoryDto(1L,"Fiction1", "Fiction books1");
    private static final CategoryDto CATEGORY_DTO_2 =
            new CategoryDto(2L,"Fiction2", "Fiction books2");
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
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/add-three-books-to-books-table.sql"));
        }
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get all books")
    public void getAll_GivenBooksInTheCatalog_ShouldReturnAllBooks() throws Exception {
        List<BookDto> expected = getBookDtos();
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(3, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get book by id")
    public void getBookById_ValidBook_Success() throws Exception {
        List<BookDto> expected = getBookDtos();
        MvcResult result = mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/remove-one-book-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new book")
    public void createBook_ValidRequestDto_Success() throws Exception {
        BookDto expected = getFirstBookDto().setIsbn("Isbn4");
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto()
                .setTitle(expected.getTitle())
                .setAuthor(expected.getAuthor())
                .setIsbn(expected.getIsbn())
                .setPrice(expected.getPrice())
                .setDescription(expected.getDescription())
                .setCoverImage(expected.getCoverImage())
                .setCategories(expected.getCategories());
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-book-from-books-table.sql"));
        }
    }

    private BookDto getFirstBookDto() {
        Set<CategoryDto> categoryDtos1 = new HashSet<>();
        categoryDtos1.add(CATEGORY_DTO_1);
        return new BookDto()
                .setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("Isbn1")
                .setPrice(BigDecimal.valueOf(120))
                .setDescription("Description1")
                .setCoverImage("Cover Image1")
                .setCategories(categoryDtos1);
    }

    private BookDto getSecondBookDto() {
        Set<CategoryDto> categoryDtos2 = new HashSet<>();
        categoryDtos2.add(CATEGORY_DTO_1);
        categoryDtos2.add(CATEGORY_DTO_2);
        return new BookDto()
                .setId(2L)
                .setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("Isbn2")
                .setPrice(BigDecimal.valueOf(220))
                .setDescription("Description2")
                .setCoverImage("Cover Image2")
                .setCategories(categoryDtos2);
    }

    private BookDto getThirdBookDto() {
        Set<CategoryDto> categoryDtos3 = new HashSet<>();
        categoryDtos3.add(CATEGORY_DTO_2);
        return new BookDto()
                .setId(3L)
                .setTitle("Title3")
                .setAuthor("Author3")
                .setIsbn("Isbn3")
                .setPrice(BigDecimal.valueOf(320))
                .setDescription("Description3")
                .setCoverImage("Cover Image3")
                .setCategories(categoryDtos3);
    }

    private List<BookDto> getBookDtos() {
        List<BookDto> expected = new ArrayList<>();
        expected.add(getFirstBookDto());
        expected.add(getSecondBookDto());
        expected.add(getThirdBookDto());
        return expected;
    }
}
