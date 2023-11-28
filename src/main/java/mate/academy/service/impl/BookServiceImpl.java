package mate.academy.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookSearchParameters;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.dto.category.CategoryDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSpecificationBuilder;
import mate.academy.repository.category.CategoryRepository;
import mate.academy.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Set<Long> categoryIdCreate = bookDto.getCategories().stream()
                .map(CategoryDto::id)
                .collect(Collectors.toSet());
        List<Category> allById = categoryRepository.findAllById(categoryIdCreate);
        if (allById.isEmpty() || allById.size() != categoryIdCreate.size()) {
            throw new EntityNotFoundException("One of these id categories: "
                    + categoryIdCreate + " is not exist");
        }
        Book book = bookMapper.toEntity(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository
                .findAll(pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public void updateById(Long id, CreateBookRequestDto bookDto) {
        Book beforeUpdate = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't update book by id "
                        + id + " because can't find it"));
        Set<Long> categoryIdNew = bookDto.getCategories().stream()
                .map(CategoryDto::id)
                .collect(Collectors.toSet());
        List<Category> categories = categoryRepository.findAllById(categoryIdNew);
        if (categories.isEmpty() || categories.size() != categoryIdNew.size()) {
            throw new EntityNotFoundException("Cant update. One of these id categories: "
                    + categoryIdNew + " is not exist");
        }
        Set<Category> newCategories = bookDto.getCategories().stream()
                .map(categoryMapper::toEntityUpdate)
                .collect(Collectors.toSet());
        beforeUpdate.setCategories(newCategories);
        beforeUpdate.setTitle(bookDto.getTitle());
        beforeUpdate.setAuthor(bookDto.getAuthor());
        beforeUpdate.setIsbn(bookDto.getIsbn());
        beforeUpdate.setPrice(bookDto.getPrice());
        beforeUpdate.setDescription(bookDto.getDescription());
        beforeUpdate.setCoverImage(bookDto.getCoverImage());
        bookRepository.save(beforeUpdate);
    }

    @Override
    public List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
