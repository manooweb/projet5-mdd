package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.system.config.MddProperties;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCookieService {

  public static final String COOKIE_NAME = "MDD_AUTH_TOKEN";

  private final MddProperties properties;

  public AuthenticationCookieService(MddProperties properties) {
    this.properties = properties;
  }

  public void addAuthenticationCookie(HttpHeaders headers, String token) {
    ResponseCookie cookie =
        ResponseCookie.from(COOKIE_NAME, token)
            .httpOnly(true)
            .secure(properties.getJwt().isSecureCookie())
            .path("/")
            .sameSite("Lax")
            .maxAge(properties.getJwt().getExpiration())
            .build();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public void removeAuthenticationCookie(HttpHeaders headers) {
    ResponseCookie cookie =
        ResponseCookie.from(COOKIE_NAME, "")
            .httpOnly(true)
            .secure(properties.getJwt().isSecureCookie())
            .path("/")
            .sameSite("Lax")
            .maxAge(Duration.ZERO)
            .build();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
