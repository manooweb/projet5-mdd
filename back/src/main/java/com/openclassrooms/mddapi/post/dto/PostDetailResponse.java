package com.openclassrooms.mddapi.post.dto;

import com.openclassrooms.mddapi.comment.dto.CommentResponse;
import com.openclassrooms.mddapi.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * Detailed post representation including its visible comments.
 *
 * @param id persistent post identifier
 * @param title post title
 * @param content post body
 * @param author public display name of the author
 * @param topic topic name
 * @param createdAt UTC creation timestamp
 * @param comments comments visible on the post
 */
@Schema(description = "A post with its comments.")
public record PostDetailResponse(
    @Schema(example = "1") Long id,
    @Schema(example = "Créer une application avec Spring Boot") String title,
    @Schema(example = "Le contenu complet de l'article.") String content,
    @Schema(example = "demo") String author,
    @Schema(example = "Spring Boot") String topic,
    @Schema(example = "2026-08-23T10:15:30Z") Instant createdAt,
    List<CommentResponse> comments) {

  public static PostDetailResponse from(Post post, List<CommentResponse> comments) {
    return new PostDetailResponse(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getAuthor().getUsername(),
        post.getTopic().getName(),
        post.getCreatedAt(),
        comments);
  }
}
