package com.openclassrooms.mddapi.topic.dto;

import com.openclassrooms.mddapi.topic.domain.Topic;
import io.swagger.v3.oas.annotations.media.Schema;

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
