package com.openclassrooms.mddapi.post.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.comment.domain.Comment;
import com.openclassrooms.mddapi.comment.dto.CommentResponse;
import com.openclassrooms.mddapi.comment.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.comment.notification.CommentCreatedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implements post-feed, post-detail and comment business operations. */
@Service
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final TopicRepository topicRepository;
  private final UserAccountRepository userAccountRepository;
  private final MddProperties properties;
  private final ApplicationEventPublisher eventPublisher;

  public PostService(
      PostRepository postRepository,
      CommentRepository commentRepository,
      TopicRepository topicRepository,
      UserAccountRepository userAccountRepository,
      MddProperties properties,
      ApplicationEventPublisher eventPublisher) {
    this.postRepository = postRepository;
    this.commentRepository = commentRepository;
    this.topicRepository = topicRepository;
    this.userAccountRepository = userAccountRepository;
    this.properties = properties;
    this.eventPublisher = eventPublisher;
  }

  /**
   * Creates a post for an existing user in an existing topic.
   *
   * @param userId authenticated author identifier
   * @param request validated post content and target topic
   * @throws ApiException when the user or topic does not exist
   */
  @Transactional
  public void createPost(Long userId, CreatePostRequest request) {
    UserAccount author = findAuthor(userId);
    Topic topic = findTopic(request.topicId());
    postRepository.save(Post.create(author, topic, request.title(), request.content()));
  }

  /**
   * Returns posts from the topics followed by a user.
   *
   * @param userId authenticated user identifier
   * @param sort chronological direction: {@code asc} or {@code desc}
   * @return subscribed-topic posts ordered by creation date
   * @throws ApiException when {@code sort} is not supported
   */
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

  /**
   * Returns a post and its comments when it belongs to a subscribed topic.
   *
   * @param userId authenticated user identifier
   * @param postId requested post identifier
   * @return post detail and comments
   * @throws ApiException when the post is not visible to the user
   */
  @Transactional(readOnly = true)
  public PostDetailResponse getPost(Long userId, Long postId) {
    Post post = findSubscribedPost(userId, postId);
    List<CommentResponse> comments =
        commentRepository.findAllByPostIdWithAuthor(postId).stream()
            .map(CommentResponse::from)
            .toList();

    return PostDetailResponse.from(post, comments);
  }

  /**
   * Persists a comment and schedules an email notification after transaction commit.
   *
   * @param userId authenticated comment author identifier
   * @param postId commented post identifier
   * @param request validated comment content
   * @throws ApiException when the user or post is not found or visible
   */
  @Transactional
  public void createComment(Long userId, Long postId, CreateCommentRequest request) {
    UserAccount author = findAuthor(userId);
    Post post = findSubscribedPost(userId, postId);
    commentRepository.save(Comment.create(author, post, request.content()));
    eventPublisher.publishEvent(
        new CommentCreatedEvent(post.getAuthor().getEmail(), post.getTitle()));
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

  private Post findSubscribedPost(Long userId, Long postId) {
    return postRepository
        .findByIdForSubscribedTopic(postId, userId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.NOT_FOUND,
                    ApiErrorCode.RESOURCE_NOT_FOUND,
                    properties.getMessages().getErrors().getResourceNotFound()));
  }
}
