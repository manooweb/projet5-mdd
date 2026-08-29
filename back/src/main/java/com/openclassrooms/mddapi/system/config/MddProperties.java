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

  /** JWT signing and cookie settings. */
  @Getter
  @Setter
  public static class Jwt {

    @NotBlank private String secret;

    private Duration expiration;

    private boolean secureCookie;
  }

  /** Outgoing email sender and notification-template settings. */
  @Getter
  @Setter
  public static class Mail {

    @NotBlank private String from;

    @Valid private CommentNotification commentNotification = new CommentNotification();
  }

  /** Content of the email sent after a comment is published. */
  @Getter
  @Setter
  public static class CommentNotification {

    @NotBlank private String subject;

    @NotBlank private String body;
  }

  /** Localized application messages exposed by API errors. */
  @Getter
  @Setter
  public static class Messages {

    @NotBlank private String duplicateIdentity;

    @NotBlank private String invalidCredentials;

    @Valid private Validation validation = new Validation();

    @Valid private Errors errors = new Errors();
  }

  /** Messages associated with invalid request data. */
  @Getter
  @Setter
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

  /** Messages associated with API and security failures. */
  @Getter
  @Setter
  public static class Errors {

    @NotBlank private String authenticationRequired;

    @NotBlank private String accessDenied;

    @NotBlank private String resourceNotFound;

    @NotBlank private String unexpected;
  }
}
