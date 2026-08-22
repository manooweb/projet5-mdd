package com.openclassrooms.mddapi.user.dto;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The authenticated user's public profile.")
public record CurrentUserResponse(
    @Schema(example = "1") Long id,
    @Schema(example = "demo") String username,
    @Schema(example = "demo@mdd.net") String email) {

  public static CurrentUserResponse from(UserAccount user) {
    return new CurrentUserResponse(user.getId(), user.getUsername(), user.getEmail());
  }
}
