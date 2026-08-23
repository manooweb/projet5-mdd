package com.openclassrooms.mddapi.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
    @NotNull Long topicId, @NotBlank @Size(max = 255) String title, @NotBlank String content) {}
