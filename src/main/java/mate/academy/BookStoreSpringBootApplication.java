package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreSpringBootApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreSpringBootApplication.class);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book1 = new Book();
            book1.setTitle("title1");
            book1.setAuthor("author1");
            book1.setIsbn("isbn1");
            book1.setPrice(BigDecimal.valueOf(10));
            book1.setDescription("description1");
            book1.setCoverImage("coverImage1");
            Book book2 = new Book();
            book2.setTitle("title1");
            book2.setAuthor("author1");
            book2.setIsbn("isbn2");
            book2.setPrice(BigDecimal.valueOf(10));
            book2.setDescription("description1");
            book2.setCoverImage("coverImage1");
            bookService.save(book1);
            bookService.save(book2);
            System.out.println(bookService.findAll());
        };
    }
}
