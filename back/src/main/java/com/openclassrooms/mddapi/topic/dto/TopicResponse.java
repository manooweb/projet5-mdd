package com.openclassrooms.mddapi.topic.dto;

import com.openclassrooms.mddapi.topic.domain.Topic;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Topic data enriched with the authenticated user's subscription status.
 *
 * @param id persistent topic identifier
 * @param name topic display name
 * @param description topic purpose
 * @param subscribed whether the authenticated user follows the topic
 */
@Schema(description = "A topic with the subscription status of the authenticated user.")
public record TopicResponse(
    @Schema(example = "1") Long id,
    @Schema(example = "Java") String name,
    @Schema(example = "Discussions about Java and its ecosystem.") String description,
    @Schema(example = "false") boolean subscribed) {

  public static TopicResponse from(Topic topic, boolean subscribed) {
    return new TopicResponse(topic.getId(), topic.getName(), topic.getDescription(), subscribed);
  }
}
