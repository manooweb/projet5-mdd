package com.openclassrooms.mddapi.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank @Size(max = 255) String login,
    @Schema(example = "stringstring") @NotBlank @Size(max = 72) String password) {}
