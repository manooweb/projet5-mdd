package com.openclassrooms.mddapi.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("mdd.authentication.messages")
public class AuthenticationMessagesProperties {

  private String duplicateIdentity;
  private Validation validation = new Validation();

  @Getter
  @Setter
  public static class Validation {

    private String invalidRequest;
    private String usernameRequired;
    private String usernameMaxSize;
    private String emailRequired;
    private String emailInvalid;
    private String emailMaxSize;
    private String passwordRequired;
    private String passwordSize;
  }
}
