package com.openclassrooms.mddapi.user.controller;

import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import com.openclassrooms.mddapi.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
}
