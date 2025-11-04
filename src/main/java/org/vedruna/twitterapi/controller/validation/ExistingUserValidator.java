package org.vedruna.twitterapi.controller.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vedruna.twitterapi.persistance.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida que exista el usuario con el id dado (userId).
 */
@Component
public class ExistingUserValidator implements ConstraintValidator<org.vedruna.twitterapi.controller.validation.ExistingUser, Integer> {

    private final UserRepository userRepository;

    @Autowired
    public ExistingUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(org.vedruna.twitterapi.controller.validation.ExistingUser constraintAnnotation) { }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return userRepository.existsById(value);
    }
}
