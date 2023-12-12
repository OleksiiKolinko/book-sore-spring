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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static Category CATEGORY;
    private static CategoryDto CATEGORY_DTO;
    private static Set<Category> CATEGORIES;
    private static Set<CategoryDto> CATEGORY_DTOS;
    private static Book BOOK;
    private static BookDto BOOK_DTO;
    private static CreateBookRequestDto CREATE_BOOK_DTO;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    static void beforeAll() {
        CATEGORY = new Category();
        CATEGORY.setId(1L);
        CATEGORY.setName("Fiction");
        CATEGORY.setDescription("Fiction books");
        CATEGORY_DTO = new CategoryDto(CATEGORY.getId(),
                CATEGORY.getName(), CATEGORY.getDescription());
        CATEGORIES = new HashSet<>();
        CATEGORIES.add(CATEGORY);
        CATEGORY_DTOS = new HashSet<>();
        CATEGORY_DTOS.add(CATEGORY_DTO);
        BOOK = new Book();
        BOOK.setId(1L);
        BOOK.setTitle("Title1");
        BOOK.setAuthor("Author1");
        BOOK.setIsbn("Isbn1");
        BOOK.setPrice(BigDecimal.valueOf(100));
        BOOK.setDescription("Description1");
        BOOK.setCoverImage("Cover Image1");
        BOOK.setCategories(CATEGORIES);
        BOOK_DTO = new BookDto();
        BOOK_DTO.setId(BOOK.getId());
        BOOK_DTO.setTitle(BOOK.getTitle());
        BOOK_DTO.setAuthor(BOOK.getAuthor());
        BOOK_DTO.setIsbn(BOOK.getIsbn());
        BOOK_DTO.setPrice(BOOK.getPrice());
        BOOK_DTO.setDescription(BOOK.getDescription());
        BOOK_DTO.setCoverImage(BOOK.getCoverImage());
        BOOK_DTO.setCategories(CATEGORY_DTOS);
        CREATE_BOOK_DTO = new CreateBookRequestDto();
        CREATE_BOOK_DTO.setTitle(BOOK.getTitle());
        CREATE_BOOK_DTO.setAuthor(BOOK.getAuthor());
        CREATE_BOOK_DTO.setIsbn(BOOK.getIsbn());
        CREATE_BOOK_DTO.setPrice(BOOK.getPrice());
        CREATE_BOOK_DTO.setDescription(BOOK.getDescription());
        CREATE_BOOK_DTO.setCoverImage(BOOK.getCoverImage());
        CREATE_BOOK_DTO.setCategories(CATEGORY_DTOS);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ReturnAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(BOOK);
        Page<Book> booksPage = new PageImpl<>(books,pageable,books.size());
        when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(BOOK_DTO);
        List<BookDto> bookDtos = bookService.findAll(pageable);
        assertThat(bookDtos).hasSize(1);
        assertThat(bookDtos.get(0)).isEqualTo(BOOK_DTO);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(BOOK);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateBookRequestDto_returnBookDto() {
        when(bookMapper.toEntity(any(CreateBookRequestDto.class))).thenReturn(BOOK);
        when(bookRepository.save(any(Book.class))).thenReturn(BOOK);
        when(bookMapper.toDto(any(Book.class))).thenReturn(BOOK_DTO);
        BookDto savedBookDto = bookService.save(CREATE_BOOK_DTO);
        assertThat(savedBookDto).isEqualTo(BOOK_DTO);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toDto(any(Book.class));
        verify(bookMapper, times(1)).toEntity(any(CreateBookRequestDto.class));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify the correct book was return when book exists")
    public void findById_WithValidBookId_ShouldReturnValidBook() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(BOOK));
        when(bookMapper.toDto(any(Book.class))).thenReturn(BOOK_DTO);
        BookDto actual = bookService.findById(BOOK.getId());
        assertEquals(BOOK_DTO, actual);
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
}
