package com.openclassrooms.mddapi.system.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.mddapi.authentication.dto.RegisterRequest;
import com.openclassrooms.mddapi.user.dto.UpdateCurrentUserRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ValidPasswordValidatorTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAcceptACompliantRegistrationPassword() {
    RegisterRequest request = new RegisterRequest("manu", "manu@example.test", "Pass1!wd");

    assertThat(validator.validate(request)).isEmpty();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("invalidPasswords")
  void shouldRejectAnInvalidPassword(String reason, String password) {
    RegisterRequest request = new RegisterRequest("manu", "manu@example.test", password);

    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void shouldAllowAnEmptyPasswordOnlyForAProfileUpdate() {
    RegisterRequest registration = new RegisterRequest("manu", "manu@example.test", "");
    UpdateCurrentUserRequest profileUpdate =
        new UpdateCurrentUserRequest("manu", "manu@example.test", "");

    assertThat(validator.validate(registration)).isNotEmpty();
    assertThat(validator.validate(profileUpdate)).isEmpty();
  }

  private static Stream<Arguments> invalidPasswords() {
    return Stream.of(
        Arguments.of("missing uppercase letter", "password1!"),
        Arguments.of("missing lowercase letter", "PASSWORD1!"),
        Arguments.of("missing digit", "Password!"),
        Arguments.of("missing special character", "Password1"),
        Arguments.of("below minimum length", "Pass1!"),
        Arguments.of("above maximum length", "Password1!" + "a".repeat(63)));
  }
}
