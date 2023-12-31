package mate.academy.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemDto(@NotNull Long bookId, @Positive int quantity) {
}
