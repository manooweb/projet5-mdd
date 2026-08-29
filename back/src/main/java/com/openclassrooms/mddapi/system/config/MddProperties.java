package com.openclassrooms.mddapi.system.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Binds and validates the application settings under the {@code mdd} configuration prefix. */
@Getter
@Setter
@Validated
@ConfigurationProperties("mdd")
public class MddProperties {

  @Valid private Jwt jwt = new Jwt();

  @Valid private Mail mail = new Mail();

  @Valid private Messages messages = new Messages();

  @Getter
  @Setter
  /** JWT signing and cookie settings. */
  public static class Jwt {

    @NotBlank private String secret;

    private Duration expiration;

    private boolean secureCookie;
  }

  @Getter
  @Setter
  /** Outgoing email sender settings. */
  public static class Mail {

    @NotBlank private String from;
  }

  @Getter
  @Setter
  /** Localized application messages exposed by API errors. */
  public static class Messages {

    @NotBlank private String duplicateIdentity;

    @NotBlank private String invalidCredentials;

    @Valid private Validation validation = new Validation();

    @Valid private Errors errors = new Errors();
  }

  @Getter
  @Setter
  /** Messages associated with invalid request data. */
  public static class Validation {

    @NotBlank private String invalidRequest;

    @NotBlank private String usernameRequired;

    @NotBlank private String usernameMaxSize;

    @NotBlank private String emailRequired;

    @NotBlank private String emailInvalid;

    @NotBlank private String emailMaxSize;

    @NotBlank private String passwordRequired;

    @NotBlank private String passwordSize;
  }

  @Getter
  @Setter
  /** Messages associated with API and security failures. */
  public static class Errors {

    @NotBlank private String authenticationRequired;

    @NotBlank private String accessDenied;

    @NotBlank private String resourceNotFound;

    @NotBlank private String unexpected;
  }
}
