package mate.academy.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Email(message = "Please provide a valid email address")
        String email,
        @Pattern(regexp = "^[0-9a-zA-Z]+$")
        @NotBlank(message = "Password is mandatory")
        @Length(min = 8, max = 30, message =
                " must be more or equals than  8 and less or equals than 30 symbols")
        String password
) {
}
