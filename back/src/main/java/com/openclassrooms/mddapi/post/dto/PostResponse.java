package com.openclassrooms.mddapi.post.dto;

import com.openclassrooms.mddapi.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Compact post representation displayed in the subscribed-post feed.
 *
 * @param id persistent post identifier
 * @param title post title
 * @param content post body
 * @param author public display name of the author
 * @param topic topic name
 * @param createdAt UTC creation timestamp
 */
@Schema(description = "A post displayed in the articles list.")
public record PostResponse(
    @Schema(example = "1") Long id,
    @Schema(example = "Créer une application avec Spring Boot") String title,
    @Schema(example = "Le contenu de l'article.") String content,
    @Schema(example = "demo") String author,
    @Schema(example = "Spring Boot") String topic,
    @Schema(example = "2026-08-23T10:15:30Z") Instant createdAt) {

  public static PostResponse from(Post post) {
    return new PostResponse(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getAuthor().getUsername(),
        post.getTopic().getName(),
        post.getCreatedAt());
  }
}
