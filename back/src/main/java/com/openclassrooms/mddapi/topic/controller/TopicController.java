package com.openclassrooms.mddapi.topic.controller;

import com.openclassrooms.mddapi.topic.dto.TopicResponse;
import com.openclassrooms.mddapi.topic.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes authenticated topic listing and subscription management endpoints. */
@RestController
public class TopicController {

  private final TopicService topicService;

  public TopicController(TopicService topicService) {
    this.topicService = topicService;
  }

  @Operation(summary = "List topics with the current user's subscription status")
  @ApiResponse(responseCode = "200", description = "Topics returned.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @GetMapping("/api/topics")
  /**
   * Lists topics with subscription state for the current user.
   *
   * @param authentication authenticated session containing the current user identifier
   * @return all available topics and their subscription state
   */
  List<TopicResponse> getTopics(Authentication authentication) {
    return topicService.getTopics(Long.valueOf(authentication.getName()));
  }

  @Operation(summary = "Subscribe the current user to a topic")
  @ApiResponse(responseCode = "204", description = "Subscription created or already existed.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "404", description = "Topic not found.")
  @PostMapping("/api/topics/{topicId}/subscription")
  /**
   * Subscribes the current user to a topic.
   *
   * @param topicId existing topic identifier
   * @param authentication authenticated session containing the current user identifier
   * @return a {@code 204 No Content} response
   */
  ResponseEntity<Void> subscribe(@PathVariable Long topicId, Authentication authentication) {
    topicService.subscribe(Long.valueOf(authentication.getName()), topicId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Unsubscribe the current user from a topic")
  @ApiResponse(responseCode = "204", description = "Subscription removed if it existed.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @DeleteMapping("/api/topics/{topicId}/subscription")
  /**
   * Removes the current user's subscription from a topic.
   *
   * @param topicId topic identifier
   * @param authentication authenticated session containing the current user identifier
   * @return a {@code 204 No Content} response
   */
  ResponseEntity<Void> unsubscribe(@PathVariable Long topicId, Authentication authentication) {
    topicService.unsubscribe(Long.valueOf(authentication.getName()), topicId);
    return ResponseEntity.noContent().build();
  }
}
