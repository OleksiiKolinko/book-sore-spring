package mate.academy.dto.book;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.dto.category.CategoryDto;

@Data
@Accessors(chain = true)
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Set<CategoryDto> categories;
}
