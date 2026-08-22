package com.openclassrooms.mddapi.system.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("mdd")
public class MddProperties {

  @Valid private Jwt jwt = new Jwt();

  @Valid private Messages messages = new Messages();

  @Getter
  @Setter
  public static class Jwt {

    @NotBlank private String secret;

    private Duration expiration;

    private boolean secureCookie;
  }

  @Getter
  @Setter
  public static class Messages {

    @NotBlank private String duplicateIdentity;

    @NotBlank private String invalidCredentials;

    @Valid private Validation validation = new Validation();

    @Valid private Errors errors = new Errors();
  }

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

  @Getter
  @Setter
  public static class Errors {

    @NotBlank private String authenticationRequired;

    @NotBlank private String accessDenied;

    @NotBlank private String resourceNotFound;

    @NotBlank private String unexpected;
  }
}
