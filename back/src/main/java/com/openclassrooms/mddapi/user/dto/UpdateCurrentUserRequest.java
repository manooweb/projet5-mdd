package com.openclassrooms.mddapi.user.dto;

import com.openclassrooms.mddapi.system.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Validated profile-update payload; an empty password preserves the current password.
 *
 * @param username required unique display name
 * @param email required unique email address in a valid format
 * @param password empty to preserve it, otherwise a password meeting the configured complexity
 *     rules
 */
public record UpdateCurrentUserRequest(
    @Schema(example = "demo") @NotBlank @Size(max = 255) String username,
    @Schema(example = "demo@mdd.net") @NotBlank @Email @Size(max = 255) String email,
    @Schema(example = "Pass1!wd") @NotNull @ValidPassword(allowEmpty = true) String password) {}
