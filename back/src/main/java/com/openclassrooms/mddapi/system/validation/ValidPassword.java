package com.openclassrooms.mddapi.system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates the MDD password policy: 8 to 72 characters containing a digit, a lowercase letter, an
 * uppercase letter, and a special character.
 *
 * <p>When {@link #allowEmpty()} is enabled, an empty value means that the existing password must be
 * preserved.
 */
@Documented
@Constraint(validatedBy = ValidPasswordValidator.class)
@Target({
  ElementType.FIELD,
  ElementType.METHOD,
  ElementType.PARAMETER,
  ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

  /** Whether an empty password is valid and means that the current password is preserved. */
  boolean allowEmpty() default false;

  /** Default validation message. */
  String message() default "must meet the password policy";

  /** Validation groups supported by Jakarta Bean Validation. */
  Class<?>[] groups() default {};

  /** Payload types supported by Jakarta Bean Validation. */
  Class<? extends Payload>[] payload() default {};
}
