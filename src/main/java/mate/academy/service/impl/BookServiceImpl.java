package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import mate.academy.service.BookService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository
                .findAll()
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
        beforeUpdate.setTitle(bookDto.getTitle());
        beforeUpdate.setAuthor(bookDto.getAuthor());
        beforeUpdate.setIsbn(bookDto.getIsbn());
        beforeUpdate.setPrice(bookDto.getPrice());
        beforeUpdate.setDescription(bookDto.getDescription());
        beforeUpdate.setCoverImage(bookDto.getCoverImage());
        bookRepository.save(beforeUpdate);
    }
}
