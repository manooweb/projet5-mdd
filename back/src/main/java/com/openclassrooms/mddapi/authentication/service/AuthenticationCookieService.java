package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.config.JwtProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCookieService {

  public static final String COOKIE_NAME = "MDD_AUTH_TOKEN";

  private final JwtProperties properties;

  public AuthenticationCookieService(JwtProperties properties) {
    this.properties = properties;
  }

  public void addAuthenticationCookie(HttpHeaders headers, String token) {
    ResponseCookie cookie =
        ResponseCookie.from(COOKIE_NAME, token)
            .httpOnly(true)
            .secure(properties.secureCookie())
            .path("/")
            .sameSite("Lax")
            .maxAge(properties.expiration())
            .build();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
