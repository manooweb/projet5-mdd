package com.openclassrooms.mddapi.comment.dto;

import com.openclassrooms.mddapi.comment.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "A comment displayed below a post.")
public record CommentResponse(
    @Schema(example = "demo") String author,
    @Schema(example = "Un commentaire utile.") String content,
    @Schema(example = "2026-08-23T10:20:30Z") Instant createdAt) {

  public static CommentResponse from(Comment comment) {
    return new CommentResponse(
        comment.getAuthor().getUsername(), comment.getContent(), comment.getCreatedAt());
  }
}
