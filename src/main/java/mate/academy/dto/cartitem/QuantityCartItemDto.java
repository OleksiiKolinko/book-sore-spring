package mate.academy.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record QuantityCartItemDto(@Positive int quantity) {
}
