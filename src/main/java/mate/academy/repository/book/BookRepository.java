package mate.academy.repository.book;

import java.util.List;
import mate.academy.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query(value = "SELECT * FROM books b JOIN books_categories bc ON b.id = bc.book_id "
            + "WHERE bc.category_id = ?", nativeQuery = true)
    List<Book> findAllByCategoryId(Long categoryId);
}
