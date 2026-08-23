package com.openclassrooms.mddapi.post.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.comment.dto.CommentResponse;
import com.openclassrooms.mddapi.comment.repository.CommentRepository;
import com.openclassrooms.mddapi.post.domain.Post;
import com.openclassrooms.mddapi.post.dto.CreatePostRequest;
import com.openclassrooms.mddapi.post.dto.PostDetailResponse;
import com.openclassrooms.mddapi.post.dto.PostResponse;
import com.openclassrooms.mddapi.post.repository.PostRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.topic.domain.Topic;
import com.openclassrooms.mddapi.topic.repository.TopicRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final TopicRepository topicRepository;
  private final UserAccountRepository userAccountRepository;
  private final MddProperties properties;

  public PostService(
      PostRepository postRepository,
      CommentRepository commentRepository,
      TopicRepository topicRepository,
      UserAccountRepository userAccountRepository,
      MddProperties properties) {
    this.postRepository = postRepository;
    this.commentRepository = commentRepository;
    this.topicRepository = topicRepository;
    this.userAccountRepository = userAccountRepository;
    this.properties = properties;
  }

  @Transactional
  public void createPost(Long userId, CreatePostRequest request) {
    UserAccount author = findAuthor(userId);
    Topic topic = findTopic(request.topicId());
    postRepository.save(Post.create(author, topic, request.title(), request.content()));
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getPosts(Long userId, String sort) {
    Sort.Direction sortDirection =
        switch (sort) {
          case "asc" -> Sort.Direction.ASC;
          case "desc" -> Sort.Direction.DESC;
          default ->
              throw new ApiException(
                  HttpStatus.BAD_REQUEST,
                  ApiErrorCode.INVALID_REQUEST,
                  properties.getMessages().getValidation().getInvalidRequest());
        };
    List<Post> posts =
        postRepository.findAllForSubscribedTopics(userId, Sort.by(sortDirection, "createdAt"));

    return posts.stream().map(PostResponse::from).toList();
  }

  @Transactional(readOnly = true)
  public PostDetailResponse getPost(Long userId, Long postId) {
    Post post =
        postRepository
            .findByIdForSubscribedTopic(postId, userId)
            .orElseThrow(
                () ->
                    new ApiException(
                        HttpStatus.NOT_FOUND,
                        ApiErrorCode.RESOURCE_NOT_FOUND,
                        properties.getMessages().getErrors().getResourceNotFound()));
    List<CommentResponse> comments =
        commentRepository.findAllByPostIdWithAuthor(postId).stream()
            .map(CommentResponse::from)
            .toList();

    return PostDetailResponse.from(post, comments);
  }

  private UserAccount findAuthor(Long userId) {
    return userAccountRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    ApiErrorCode.AUTHENTICATION_REQUIRED,
                    properties.getMessages().getErrors().getAuthenticationRequired()));
  }

  private Topic findTopic(Long topicId) {
    return topicRepository
        .findById(topicId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.NOT_FOUND,
                    ApiErrorCode.RESOURCE_NOT_FOUND,
                    properties.getMessages().getErrors().getResourceNotFound()));
  }
}
