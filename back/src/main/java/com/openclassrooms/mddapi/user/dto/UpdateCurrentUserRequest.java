package com.openclassrooms.mddapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCurrentUserRequest(
    @Schema(example = "demo") @NotBlank @Size(max = 255) String username,
    @Schema(example = "demo@mdd.net") @NotBlank @Email @Size(max = 255) String email,
    @Schema(example = "Pass1!wd")
        @NotNull
        @Size(max = 72)
        @Pattern(regexp = "^(?:$|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72})$")
        String password) {}
