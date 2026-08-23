package com.openclassrooms.mddapi.user.controller;

import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import com.openclassrooms.mddapi.user.dto.UpdateCurrentUserRequest;
import com.openclassrooms.mddapi.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentUserController {

  private final CurrentUserService currentUserService;

  public CurrentUserController(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  @Operation(summary = "Get the current authenticated user")
  @ApiResponse(responseCode = "200", description = "Current user returned.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @GetMapping("/api/users/me")
  CurrentUserResponse currentUser(Authentication authentication) {
    return currentUserService.getCurrentUser(Long.valueOf(authentication.getName()));
  }

  @Operation(summary = "Update the current authenticated user")
  @ApiResponse(responseCode = "204", description = "User profile updated.")
  @ApiResponse(responseCode = "400", description = "Invalid profile data.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "409", description = "Username or email is already used.")
  @PatchMapping("/api/users/me")
  ResponseEntity<Void> updateCurrentUser(
      @Valid @RequestBody UpdateCurrentUserRequest request, Authentication authentication) {
    currentUserService.updateCurrentUser(Long.valueOf(authentication.getName()), request);
    return ResponseEntity.noContent().build();
  }
}
