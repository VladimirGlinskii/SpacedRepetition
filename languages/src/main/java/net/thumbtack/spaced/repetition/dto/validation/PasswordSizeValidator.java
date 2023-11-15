package net.thumbtack.spaced.repetition.dto.validation;

import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordSizeValidator implements ConstraintValidator<PasswordSize, String> {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null
                && s.length() >= applicationProperties.getMinPasswordLength()
                && s.length() <= applicationProperties.getMaxPasswordLength();
    }
}
