package com.openclassrooms.mddapi.authentication.controller;

import com.openclassrooms.mddapi.authentication.dto.RegisterRequest;
import com.openclassrooms.mddapi.authentication.service.AuthenticationCookieService;
import com.openclassrooms.mddapi.authentication.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

  @Operation(
      summary = "Register a user",
      description =
          """
          Creates a user account and opens an authenticated session.

          Call `GET /api/auth/csrf` once before sending this request.
          """)
  @ApiResponse(
      responseCode = "201",
      description = "User registered and authentication cookie created.",
      headers =
          @Header(
              name = "Set-Cookie",
              description = "Sets the HttpOnly MDD_AUTH_TOKEN authentication cookie.",
              schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "400", description = "Invalid registration request.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "409", description = "Username or email is already used.")
  @ApiResponse(responseCode = "500", description = "Unexpected server error.")
  @PostMapping("/api/auth/register")
  ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    String token = registrationService.register(request);
    HttpHeaders headers = new HttpHeaders();
    authenticationCookieService.addAuthenticationCookie(headers, token);
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
}
