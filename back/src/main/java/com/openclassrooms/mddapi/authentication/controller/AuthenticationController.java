package com.openclassrooms.mddapi.authentication.controller;

import com.openclassrooms.mddapi.authentication.dto.RegisterRequest;
import com.openclassrooms.mddapi.authentication.service.AuthenticationCookieService;
import com.openclassrooms.mddapi.authentication.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

  private final RegistrationService registrationService;
  private final AuthenticationCookieService authenticationCookieService;

  public AuthenticationController(
      RegistrationService registrationService,
      AuthenticationCookieService authenticationCookieService) {
    this.registrationService = registrationService;
    this.authenticationCookieService = authenticationCookieService;
  }

  @PostMapping("/api/auth/register")
  ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    String token = registrationService.register(request);
    HttpHeaders headers = new HttpHeaders();
    authenticationCookieService.addAuthenticationCookie(headers, token);
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
}
