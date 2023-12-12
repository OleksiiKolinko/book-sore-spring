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
    protected static MockMvc mockMvc;
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

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-book-from-books-table.sql"));
        }
    }

    private List<BookDto> getBookDtos() {
        final CategoryDto categoryDto1 = new CategoryDto(1L,"Fiction1", "Fiction books1");
        final CategoryDto categoryDto2 = new CategoryDto(2L,"Fiction2", "Fiction books2");
        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setTitle("Title1");
        bookDto1.setAuthor("Author1");
        bookDto1.setIsbn("Isbn1");
        bookDto1.setPrice(BigDecimal.valueOf(120));
        bookDto1.setDescription("Description1");
        bookDto1.setCoverImage("Cover Image1");
        Set<CategoryDto> categoryDtos1 = new HashSet<>();
        categoryDtos1.add(categoryDto1);
        bookDto1.setCategories(categoryDtos1);
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Title2");
        bookDto2.setAuthor("Author2");
        bookDto2.setIsbn("Isbn2");
        bookDto2.setPrice(BigDecimal.valueOf(220));
        bookDto2.setDescription("Description2");
        bookDto2.setCoverImage("Cover Image2");
        Set<CategoryDto> categoryDtos2 = new HashSet<>();
        categoryDtos2.add(categoryDto1);
        categoryDtos2.add(categoryDto2);
        bookDto2.setCategories(categoryDtos2);
        BookDto bookDto3 = new BookDto();
        bookDto3.setId(3L);
        bookDto3.setTitle("Title3");
        bookDto3.setAuthor("Author3");
        bookDto3.setIsbn("Isbn3");
        bookDto3.setPrice(BigDecimal.valueOf(320));
        bookDto3.setDescription("Description3");
        bookDto3.setCoverImage("Cover Image3");
        Set<CategoryDto> categoryDtos3 = new HashSet<>();
        categoryDtos3.add(categoryDto2);
        bookDto3.setCategories(categoryDtos3);
        List<BookDto> expected = new ArrayList<>();
        expected.add(bookDto1);
        expected.add(bookDto2);
        expected.add(bookDto3);
        return expected;
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
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle("Title1");
        bookRequestDto.setAuthor("Author1");
        bookRequestDto.setIsbn("Isbn4");
        bookRequestDto.setPrice(BigDecimal.valueOf(120));
        bookRequestDto.setDescription("Description1");
        bookRequestDto.setCoverImage("Cover Image1");
        Set<CategoryDto> categoryDtos1 = new HashSet<>();
        CategoryDto categoryDto1 = new CategoryDto(1L,"Fiction1", "Fiction books1");
        categoryDtos1.add(categoryDto1);
        bookRequestDto.setCategories(categoryDtos1);
        BookDto expected = new BookDto();
        expected.setTitle(bookRequestDto.getTitle());
        expected.setAuthor(bookRequestDto.getAuthor());
        expected.setIsbn(bookRequestDto.getIsbn());
        expected.setPrice(bookRequestDto.getPrice());
        expected.setDescription(bookRequestDto.getDescription());
        expected.setCoverImage(bookRequestDto.getCoverImage());
        expected.setCategories(categoryDtos1);
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
}
