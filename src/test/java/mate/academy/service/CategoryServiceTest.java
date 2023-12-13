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

import java.util.List;
import java.util.Optional;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CreateCategoryDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.category.CategoryRepository;
import mate.academy.service.impl.CategoryServiceImpl;
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
public class CategoryServiceTest {
    private static CreateCategoryDto CATEGORY_DTO;
    private static final Long FIRST_ID = 1L;
    private static final String FICTION = "Fiction";
    private static final String FICTION_BOOKS = "Fiction books";
    private static Category CATEGORY;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void beforeAll() {
        CATEGORY = new Category();
        CATEGORY.setId(FIRST_ID);
        CATEGORY.setName(FICTION);
        CATEGORY.setDescription(FICTION_BOOKS);
        CATEGORY_DTO = new CreateCategoryDto(FICTION, FICTION_BOOKS);
    }

    @Test
    @DisplayName("Verify the correct category was return when category exists")
    public void getById_WithValidCategoryId_ShouldReturnValidCategory() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(CATEGORY));
        when(categoryMapper.toViewModel(any(Category.class))).thenReturn(CATEGORY_DTO);
        CreateCategoryDto actual = categoryService.getById(FIRST_ID);
        assertEquals(CATEGORY_DTO, actual);
        verify(categoryRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Checks whether an exception is thrown when"
            + " a category with the corresponding id does not exist")
    public void getById_WithNonExistingCategoryId_ShouldThrowException() {
        Long categoryId = 100L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));
        String expected = "Can't find category by id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateCategoryDto_returnCreateCategoryDto() {
        when(categoryMapper.toEntity(any(CreateCategoryDto.class))).thenReturn(CATEGORY);
        when(categoryRepository.save(any(Category.class))).thenReturn(CATEGORY);
        when(categoryMapper.toViewModel(any(Category.class))).thenReturn(CATEGORY_DTO);
        CreateCategoryDto savedCategoryDto = categoryService.save(CATEGORY_DTO);
        assertThat(savedCategoryDto).isEqualTo(CATEGORY_DTO);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ReturnAllCategories() {
        CategoryDto categoryDto = new CategoryDto(FIRST_ID, FICTION, FICTION_BOOKS);
        Pageable pageable = PageRequest.of(0,10);
        List<Category> categories = List.of(CATEGORY);
        Page<Category> categoryPage = new PageImpl<>(categories,pageable,categories.size());
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);
        List<CategoryDto> categoryDtos = categoryService.findAll(pageable);
        assertThat(categoryDtos).hasSize(1);
        assertThat(categoryDtos.get(0)).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(any(Category.class));
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }
}
