package com.openclassrooms.mddapi.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(max = 255) String username,
    @NotBlank @Email @Size(max = 255) String email,
    @Schema(example = "Pass1!wd") @NotBlank @Size(min = 8, max = 72) String password) {}
