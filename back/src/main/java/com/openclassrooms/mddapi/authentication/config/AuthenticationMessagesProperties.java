package com.openclassrooms.mddapi.authentication.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("mdd.authentication.messages")
public class AuthenticationMessagesProperties {

  @NotBlank private String duplicateIdentity;

  @NotBlank private String invalidCredentials;

  @Valid private Validation validation = new Validation();

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
}
