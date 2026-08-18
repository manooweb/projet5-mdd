package com.openclassrooms.mddapi.authentication.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class AuthenticationMessagesPropertiesTest {

  @Test
  void shouldBindExpectedMessagesFromAuthenticationYaml() throws IOException {
    AuthenticationMessagesProperties messages = bindMessagesFromAuthenticationYaml();

    assertThat(messages.getDuplicateIdentity()).isEqualTo("Username or email is already used.");
    assertThat(messages.getInvalidCredentials()).isEqualTo("Invalid credentials.");
    assertThat(messages.getValidation().getInvalidRequest())
        .isEqualTo("The request contains an invalid value.");
    assertThat(messages.getValidation().getUsernameRequired()).isEqualTo("Username is required.");
    assertThat(messages.getValidation().getUsernameMaxSize().formatted(30))
        .isEqualTo("Username must not exceed 30 characters.");
    assertThat(messages.getValidation().getEmailRequired()).isEqualTo("Email is required.");
    assertThat(messages.getValidation().getEmailInvalid()).isEqualTo("Email must be valid.");
    assertThat(messages.getValidation().getEmailMaxSize().formatted(255))
        .isEqualTo("Email must not exceed 255 characters.");
    assertThat(messages.getValidation().getPasswordRequired()).isEqualTo("Password is required.");
    assertThat(messages.getValidation().getPasswordSize().formatted(8, 72))
        .isEqualTo("Password must contain between 8 and 72 characters.");
  }

  @Test
  void shouldRequireEveryMessage() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    assertThat(validator.validate(new AuthenticationMessagesProperties()))
        .extracting(violation -> violation.getPropertyPath().toString())
        .containsExactlyInAnyOrder(
            "duplicateIdentity",
            "invalidCredentials",
            "validation.invalidRequest",
            "validation.usernameRequired",
            "validation.usernameMaxSize",
            "validation.emailRequired",
            "validation.emailInvalid",
            "validation.emailMaxSize",
            "validation.passwordRequired",
            "validation.passwordSize");
  }

  private AuthenticationMessagesProperties bindMessagesFromAuthenticationYaml() {
    ClassPathResource resource = new ClassPathResource("authentication.yml");
    if (!resource.exists()) {
      throw new IllegalStateException(
          "Authentication YAML resource 'authentication.yml' was not found on the test classpath.");
    }

    List<PropertySource<?>> sources;
    try {
      sources = new YamlPropertySourceLoader().load("authentication.yml", resource);
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Authentication YAML resource 'authentication.yml' could not be loaded.", exception);
    }

    if (sources.isEmpty()) {
      throw new IllegalStateException(
          "Authentication YAML resource 'authentication.yml' does not contain a YAML document.");
    }

    PropertySource<?> source = sources.getFirst();

    return new Binder(ConfigurationPropertySources.from(source))
        .bind("mdd.authentication.messages", Bindable.of(AuthenticationMessagesProperties.class))
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Configuration prefix 'mdd.authentication.messages' is missing from authentication.yml."));
  }
}
