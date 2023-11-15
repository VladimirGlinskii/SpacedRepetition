package net.thumbtack.spaced.repetition.dto.validation;

import net.thumbtack.spaced.repetition.utils.ErrorMessages;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameSizeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NameSize {
    String message() default ErrorMessages.INCORRECT_USERNAME_LENGTH;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
