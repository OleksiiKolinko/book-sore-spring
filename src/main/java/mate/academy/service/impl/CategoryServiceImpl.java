package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CreateCategoryDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.category.CategoryRepository;
import mate.academy.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CreateCategoryDto getById(Long id) {
        return categoryMapper.toViewModel(categoryRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find category by id: " + id)));
    }

    @Override
    public CreateCategoryDto save(CreateCategoryDto categoryDto) {
        return categoryMapper
                .toViewModel(categoryRepository.save(categoryMapper.toEntity(categoryDto)));
    }

    @Override
    public CreateCategoryDto update(Long id, CreateCategoryDto categoryDto) {
        Category categoryBefore = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find category, in order to update, by id: " + id));
        categoryBefore.setName(categoryDto.name());
        categoryBefore.setDescription(categoryDto.description());
        return categoryMapper.toViewModel(categoryRepository.save(categoryBefore));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long categoryId) {
        return bookRepository.findAllByCategoryId(categoryId).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
