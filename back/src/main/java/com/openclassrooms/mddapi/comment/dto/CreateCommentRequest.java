package com.openclassrooms.mddapi.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(@NotBlank String content) {}
