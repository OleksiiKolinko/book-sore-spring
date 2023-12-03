package mate.academy.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusDto(@NotBlank String status) {
}
