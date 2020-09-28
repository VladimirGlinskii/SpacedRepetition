package net.thumbtack.spaced.repetition.dto.request.user;

import net.thumbtack.spaced.repetition.dto.validation.NameSize;
import net.thumbtack.spaced.repetition.dto.validation.PasswordSize;
import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class RegisterDtoRequest {

    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9-_.]*", message = ErrorMessages.INCORRECT_USERNAME_PATTERN)
    @NameSize
    private String username;
    @NotNull(message = ErrorMessages.NULL_EMAIL)
    @Email(message = ErrorMessages.WRONG_EMAIL_PATTERN)
    private String email;
    @PasswordSize
    private String password;

    public RegisterDtoRequest() {
    }

    public RegisterDtoRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterDtoRequest)) return false;
        RegisterDtoRequest request = (RegisterDtoRequest) o;
        return Objects.equals(getUsername(), request.getUsername()) &&
                Objects.equals(getEmail(), request.getEmail()) &&
                Objects.equals(getPassword(), request.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getEmail(), getPassword());
    }
}
