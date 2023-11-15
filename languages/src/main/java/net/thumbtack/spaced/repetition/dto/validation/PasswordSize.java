package net.thumbtack.spaced.repetition.dto.validation;

import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordSizeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordSize {
    String message() default ErrorMessages.INCORRECT_PASSWORD_LENGTH;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
