package com.openclassrooms.mddapi.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Schema(example = "demo") @NotBlank @Size(max = 255) String username,
    @Schema(example = "demo@mdd.net") @NotBlank @Email @Size(max = 255) String email,
    @Schema(example = "Pass1!wd")
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,72}$")
        String password) {}
