package net.thumbtack.spaced.repetition.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.spaced.repetition.dto.validation.NameSize;
import net.thumbtack.spaced.repetition.dto.validation.PasswordSize;
import net.thumbtack.spaced.repetition.utils.ErrorMessages;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDtoRequest {
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9-_.]*", message = ErrorMessages.INCORRECT_USERNAME_PATTERN)
    @NameSize
    private String username;

    @NotNull(message = ErrorMessages.NULL_EMAIL)
    @Email(message = ErrorMessages.WRONG_EMAIL_PATTERN)
    private String email;

    @PasswordSize
    private String password;
}
