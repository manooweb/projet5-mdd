package com.openclassrooms.mddapi.user.controller;

import com.openclassrooms.mddapi.authentication.service.AuthenticationCookieService;
import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import com.openclassrooms.mddapi.user.dto.UpdateCurrentUserRequest;
import com.openclassrooms.mddapi.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentUserController {

  private final CurrentUserService currentUserService;
  private final AuthenticationCookieService authenticationCookieService;

  public CurrentUserController(
      CurrentUserService currentUserService,
      AuthenticationCookieService authenticationCookieService) {
    this.currentUserService = currentUserService;
    this.authenticationCookieService = authenticationCookieService;
  }

  @Operation(summary = "Get the current authenticated user")
  @ApiResponse(responseCode = "200", description = "Current user returned.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @GetMapping("/api/users/me")
  CurrentUserResponse currentUser(Authentication authentication) {
    return currentUserService.getCurrentUser(Long.valueOf(authentication.getName()));
  }

  @Operation(summary = "Update the current authenticated user")
  @ApiResponse(
      responseCode = "204",
      description =
          "User profile updated. A new authentication cookie is sent when the password changes.",
      headers =
          @Header(
              name = "Set-Cookie",
              description = "Refreshes the HttpOnly MDD_AUTH_TOKEN cookie after a password change.",
              schema = @Schema(type = "string")))
  @ApiResponse(responseCode = "400", description = "Invalid profile data.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "409", description = "Username or email is already used.")
  @PatchMapping("/api/users/me")
  ResponseEntity<Void> updateCurrentUser(
      @Valid @RequestBody UpdateCurrentUserRequest request, Authentication authentication) {
    HttpHeaders headers = new HttpHeaders();
    currentUserService
        .updateCurrentUser(Long.valueOf(authentication.getName()), request)
        .ifPresent(token -> authenticationCookieService.addAuthenticationCookie(headers, token));
    return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
  }
}
