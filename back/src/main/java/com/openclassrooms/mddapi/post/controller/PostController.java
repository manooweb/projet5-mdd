package com.openclassrooms.mddapi.post.controller;

import com.openclassrooms.mddapi.post.dto.CreatePostRequest;
import com.openclassrooms.mddapi.post.dto.PostResponse;
import com.openclassrooms.mddapi.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @Operation(summary = "List posts")
  @ApiResponse(responseCode = "200", description = "Posts returned from newest to oldest.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @GetMapping("/api/posts")
  List<PostResponse> getPosts() {
    return postService.getPosts();
  }

  @Operation(summary = "Create a post")
  @ApiResponse(responseCode = "201", description = "Post created.")
  @ApiResponse(responseCode = "400", description = "Invalid post data.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "404", description = "Topic not found.")
  @PostMapping("/api/posts")
  ResponseEntity<Void> createPost(
      @Valid @RequestBody CreatePostRequest request, Authentication authentication) {
    postService.createPost(Long.valueOf(authentication.getName()), request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
