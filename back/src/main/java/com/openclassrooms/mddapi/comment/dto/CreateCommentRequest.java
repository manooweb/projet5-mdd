package com.openclassrooms.mddapi.comment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Validated payload used to add a non-empty comment to a post.
 *
 * @param content required comment text
 */
public record CreateCommentRequest(@NotBlank String content) {}
