package com.openclassrooms.mddapi.system.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

class MddPropertiesTest {

  @Test
  void shouldBindExpectedPropertiesFromMddYaml() throws IOException {
    MddProperties properties = bindPropertiesFromMddYaml();

    assertThat(properties.getJwt().getExpiration()).hasToString("PT8H");
    assertThat(properties.getJwt().isSecureCookie()).isTrue();
    assertThat(properties.getMessages().getDuplicateIdentity())
        .isEqualTo("Username or email is already used.");
    assertThat(properties.getMessages().getInvalidCredentials()).isEqualTo("Invalid credentials.");
    assertThat(properties.getMessages().getValidation().getInvalidRequest())
        .isEqualTo("The request contains an invalid value.");
    assertThat(properties.getMessages().getValidation().getUsernameRequired())
        .isEqualTo("Username is required.");
    assertThat(properties.getMessages().getValidation().getUsernameMaxSize().formatted(30))
        .isEqualTo("Username must not exceed 30 characters.");
    assertThat(properties.getMessages().getValidation().getEmailRequired())
        .isEqualTo("Email is required.");
    assertThat(properties.getMessages().getValidation().getEmailInvalid())
        .isEqualTo("Email must be valid.");
    assertThat(properties.getMessages().getValidation().getEmailMaxSize().formatted(255))
        .isEqualTo("Email must not exceed 255 characters.");
    assertThat(properties.getMessages().getValidation().getPasswordRequired())
        .isEqualTo("Password is required.");
    assertThat(properties.getMessages().getValidation().getPasswordSize().formatted(8, 72))
        .isEqualTo("Password must contain between 8 and 72 characters.");
    assertThat(properties.getMessages().getErrors().getAuthenticationRequired())
        .isEqualTo("Authentication is required.");
    assertThat(properties.getMessages().getErrors().getAccessDenied())
        .isEqualTo("Access is denied.");
    assertThat(properties.getMessages().getErrors().getUnexpected())
        .isEqualTo("An unexpected error occurred.");
  }

  @Test
  void shouldRequireEveryMessage() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    assertThat(validator.validate(new MddProperties()))
        .extracting(violation -> violation.getPropertyPath().toString())
        .containsExactlyInAnyOrder(
            "jwt.secret",
            "messages.duplicateIdentity",
            "messages.invalidCredentials",
            "messages.validation.invalidRequest",
            "messages.validation.usernameRequired",
            "messages.validation.usernameMaxSize",
            "messages.validation.emailRequired",
            "messages.validation.emailInvalid",
            "messages.validation.emailMaxSize",
            "messages.validation.passwordRequired",
            "messages.validation.passwordSize",
            "messages.errors.authenticationRequired",
            "messages.errors.accessDenied",
            "messages.errors.unexpected");
  }

  private MddProperties bindPropertiesFromMddYaml() throws IOException {
    ClassPathResource resource = new ClassPathResource("mdd.yml");
    if (!resource.exists()) {
      throw new IllegalStateException(
          "MDD YAML resource 'mdd.yml' was not found on the test classpath.");
    }

    List<PropertySource<?>> sources;
    try {
      sources = new YamlPropertySourceLoader().load("mdd.yml", resource);
    } catch (IOException exception) {
      throw new IllegalStateException(
          "MDD YAML resource 'mdd.yml' could not be loaded.", exception);
    }

    if (sources.isEmpty()) {
      throw new IllegalStateException(
          "MDD YAML resource 'mdd.yml' does not contain a YAML document.");
    }

    PropertySource<?> source = sources.getFirst();
    StandardEnvironment environment = new StandardEnvironment();
    environment
        .getPropertySources()
        .addFirst(
            new MapPropertySource(
                "mdd-properties-test",
                Map.of(
                    "MDD_JWT_SECRET", "test-secret",
                    "MDD_JWT_EXPIRATION", "PT8H",
                    "MDD_JWT_SECURE_COOKIE", "true")));

    return new Binder(
            ConfigurationPropertySources.from(source),
            new PropertySourcesPlaceholdersResolver(environment))
        .bind("mdd", Bindable.of(MddProperties.class))
        .orElseThrow(
            () -> new IllegalStateException("Configuration prefix 'mdd' is missing from mdd.yml."));
  }
}
