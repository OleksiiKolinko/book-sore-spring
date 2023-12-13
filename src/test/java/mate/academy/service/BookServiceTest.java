package mate.academy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.dto.category.CategoryDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.book.BookRepository;
import mate.academy.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final Long ID_ONE = 1L;
    private static final String TITLE = "Title1";
    private static final String AUTHOR = "Author1";
    private static final String ISBN = "Isbn1";
    private static final BigDecimal PRICE = BigDecimal.valueOf(20);
    private static final String DESCRIPTION = "Description1";
    private static final String COVER_IMAGE = "Cover Image1";
    private static final CategoryDto CATEGORY_DTO =
            new CategoryDto(1L,"Fiction", "Fiction books");
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ReturnAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = getBook();
        BookDto bookDto = getBookDto();
        List<Book> books = List.of(book);
        Page<Book> booksPage = new PageImpl<>(books,pageable,books.size());
        when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);
        List<BookDto> bookDtos = bookService.findAll(pageable);
        assertThat(bookDtos).hasSize(1);
        assertThat(bookDtos.get(0)).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateBookRequestDto_returnBookDto() {
        Book book = getBook();
        BookDto bookDto = getBookDto();
        when(bookMapper.toEntity(any(CreateBookRequestDto.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);
        BookDto savedBookDto = bookService.save(getCreateBookDto());
        assertThat(savedBookDto).isEqualTo(bookDto);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toDto(any(Book.class));
        verify(bookMapper, times(1)).toEntity(any(CreateBookRequestDto.class));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Test
    @DisplayName("Verify the correct book was return when book exists")
    public void findById_WithValidBookId_ShouldReturnValidBook() {
        Book book = getBook();
        BookDto bookDto = getBookDto();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);
        BookDto actual = bookService.findById(bookDto.getId());
        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Checks whether an exception is thrown when"
            + " a book with the corresponding id does not exist")
    public void findById_WithNonExistingCategoryId_ShouldThrowException() {
        Long bookId = 100L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.findById(bookId));
        String expected = "Can't find book by id " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    private Book getBook() {
        final Set<Category> categories = new HashSet<>();
        Category category = new Category();
        category.setId(CATEGORY_DTO.id());
        category.setName(CATEGORY_DTO.name());
        category.setDescription(CATEGORY_DTO.description());
        categories.add(category);
        Book book = new Book();
        book.setId(ID_ONE);
        book.setTitle(TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(ISBN);
        book.setPrice(PRICE);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);
        book.setCategories(categories);
        return book;
    }

    private BookDto getBookDto() {
        Set<CategoryDto> categoryDtos = new HashSet<>();
        categoryDtos.add(CATEGORY_DTO);
        return new BookDto()
                .setId(ID_ONE)
                .setTitle(TITLE)
                .setAuthor(AUTHOR)
                .setIsbn(ISBN)
                .setPrice(PRICE)
                .setDescription(DESCRIPTION)
                .setCoverImage(COVER_IMAGE)
                .setCategories(categoryDtos);
    }

    private CreateBookRequestDto getCreateBookDto() {
        return new CreateBookRequestDto()
                .setTitle(TITLE)
                .setAuthor(AUTHOR)
                .setIsbn(ISBN)
                .setPrice(PRICE)
                .setDescription(DESCRIPTION)
                .setCoverImage(COVER_IMAGE)
                .setCategories(getBookDto().getCategories());
    }
}
