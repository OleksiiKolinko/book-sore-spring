package mate.academy.service;

import java.util.List;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    void updateById(Long id, CreateBookRequestDto bookDto);
}
