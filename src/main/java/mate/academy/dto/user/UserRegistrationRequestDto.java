package mate.academy.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import mate.academy.annotation.FieldMatch;
import org.hibernate.validator.constraints.Length;

@FieldMatch(first = "password", second = "repeatPassword",
        message = " and repeat password don't match")
@Data
public class UserRegistrationRequestDto {
    @NotBlank
    @Email(message = "Please provide a valid email address")
    private String email;
    @Pattern(regexp = "^[0-9a-zA-Z]+$")
    @NotBlank(message = "Password is mandatory")
    @Length(min = 8, max = 30, message =
            " must be more or equals than  8 and less or equals than 30 symbols")
    private String password;
    private String repeatPassword;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    private String shippingAddress;
}
