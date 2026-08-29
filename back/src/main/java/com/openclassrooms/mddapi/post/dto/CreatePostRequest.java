package com.openclassrooms.mddapi.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Validated payload used to create a post in a topic.
 *
 * @param topicId required existing topic identifier
 * @param title required post title, limited to 255 characters
 * @param content required post body
 */
public record CreatePostRequest(
    @NotNull Long topicId, @NotBlank @Size(max = 255) String title, @NotBlank String content) {}
