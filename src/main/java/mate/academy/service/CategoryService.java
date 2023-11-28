package mate.academy.service;

import java.util.List;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CreateCategoryDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CreateCategoryDto getById(Long id);

    CreateCategoryDto save(CreateCategoryDto categoryDto);

    CreateCategoryDto update(Long id, CreateCategoryDto categoryDto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long categoryId);
}
