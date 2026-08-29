package com.openclassrooms.mddapi.system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Implements the {@link ValidPassword} policy through independent linear character checks. */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

  private boolean allowEmpty;

  @Override
  public void initialize(ValidPassword constraintAnnotation) {
    allowEmpty = constraintAnnotation.allowEmpty();
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) {
      return true;
    }

    if (password.isEmpty()) {
      return allowEmpty;
    }

    return password.length() >= 8
        && password.length() <= 72
        && password.chars().anyMatch(Character::isDigit)
        && password.chars().anyMatch(Character::isLowerCase)
        && password.chars().anyMatch(Character::isUpperCase)
        && password.chars().anyMatch(character -> !Character.isLetterOrDigit(character));
  }
}
