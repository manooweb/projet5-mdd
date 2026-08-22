package com.openclassrooms.mddapi.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @Schema(example = "demo") @NotBlank @Size(max = 255) String login,
    @Schema(example = "Pass1!wd") @NotBlank @Size(max = 72) String password) {}
