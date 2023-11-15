package net.thumbtack.spaced.repetition.dto.validation;

import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameSizeValidator implements ConstraintValidator<NameSize, String> {
    @Autowired
    private ApplicationProperties properties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && !s.isEmpty() && s.length() <= properties.getMaxNameLength();
    }
}
