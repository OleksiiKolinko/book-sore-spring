package mate.academy.dto.order;

import jakarta.validation.constraints.NotBlank;

public record DoOrderDto(@NotBlank String shippingAddress) {
}
