package com.openclassrooms.mddapi.post.controller;

import com.openclassrooms.mddapi.comment.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.post.dto.CreatePostRequest;
import com.openclassrooms.mddapi.post.dto.PostDetailResponse;
import com.openclassrooms.mddapi.post.dto.PostResponse;
import com.openclassrooms.mddapi.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Provides authenticated access to the subscribed-post feed, posts and comments. */
@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @Operation(summary = "List posts")
  @ApiResponse(
      responseCode = "200",
      description = "Posts returned in the requested chronological order.")
  @ApiResponse(responseCode = "400", description = "Invalid sort direction.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @GetMapping("/api/posts")
  /**
   * Lists posts from topics followed by the authenticated user.
   *
   * @param sort chronological direction, either {@code asc} or {@code desc}
   * @param authentication authenticated session containing the current user identifier
   * @return posts ordered by their creation date
   */
  List<PostResponse> getPosts(
      @Parameter(
              description = "Chronological sort direction.",
              schema =
                  @Schema(
                      type = "string",
                      allowableValues = {"asc", "desc"},
                      defaultValue = "desc"))
          @RequestParam(defaultValue = "desc")
          String sort,
      Authentication authentication) {
    return postService.getPosts(Long.valueOf(authentication.getName()), sort);
  }

  @Operation(summary = "Get a post with its comments")
  @ApiResponse(responseCode = "200", description = "Post and comments returned.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "404", description = "Post not found or topic not followed.")
  @GetMapping("/api/posts/{postId}")
  /**
   * Returns a subscribed post and its comments.
   *
   * @param postId identifier of the requested post
   * @param authentication authenticated session containing the current user identifier
   * @return the post detail response
   */
  PostDetailResponse getPost(@PathVariable Long postId, Authentication authentication) {
    return postService.getPost(Long.valueOf(authentication.getName()), postId);
  }

  @Operation(summary = "Create a comment on a post")
  @ApiResponse(responseCode = "201", description = "Comment created.")
  @ApiResponse(responseCode = "400", description = "Invalid comment data.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "404", description = "Post not found or topic not followed.")
  @PostMapping("/api/posts/{postId}/comments")
  /**
   * Adds a comment to a post visible to the authenticated user.
   *
   * @param postId identifier of the commented post
   * @param request validated comment content
   * @param authentication authenticated session containing the author identifier
   * @return a {@code 201 Created} response
   */
  ResponseEntity<Void> createComment(
      @PathVariable Long postId,
      @Valid @RequestBody CreateCommentRequest request,
      Authentication authentication) {
    postService.createComment(Long.valueOf(authentication.getName()), postId, request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "Create a post")
  @ApiResponse(responseCode = "201", description = "Post created.")
  @ApiResponse(responseCode = "400", description = "Invalid post data.")
  @ApiResponse(responseCode = "401", description = "No valid authenticated session.")
  @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token.")
  @ApiResponse(responseCode = "404", description = "Topic not found.")
  @PostMapping("/api/posts")
  /**
   * Creates a post on behalf of the authenticated user.
   *
   * @param request validated title, content and topic identifier
   * @param authentication authenticated session containing the author identifier
   * @return a {@code 201 Created} response
   */
  ResponseEntity<Void> createPost(
      @Valid @RequestBody CreatePostRequest request, Authentication authentication) {
    postService.createPost(Long.valueOf(authentication.getName()), request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
