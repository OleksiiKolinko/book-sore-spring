package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CreateCategoryDto;
import mate.academy.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryDto categoryDto);

    CreateCategoryDto toViewModel(Category category);

    Category toEntityUpdate(CategoryDto category);

}
