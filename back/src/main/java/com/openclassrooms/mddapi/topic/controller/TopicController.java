package com.openclassrooms.mddapi.topic.controller;

import com.openclassrooms.mddapi.topic.dto.TopicResponse;
import com.openclassrooms.mddapi.topic.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
  List<TopicResponse> getTopics(Authentication authentication) {
    return topicService.getTopics(Long.valueOf(authentication.getName()));
  }

  @Operation(summary = "Subscribe the current user to a topic")
  @ApiResponse(responseCode = "204", description = "Subscription created or already existed.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "404", description = "Topic not found.")
  @PostMapping("/api/topics/{topicId}/subscription")
  ResponseEntity<Void> subscribe(@PathVariable Long topicId, Authentication authentication) {
    topicService.subscribe(Long.valueOf(authentication.getName()), topicId);
    return ResponseEntity.noContent().build();
  }
}
