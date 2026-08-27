package com.openclassrooms.mddapi.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.comment.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.comment.notification.CommentCreatedEvent;
import com.openclassrooms.mddapi.comment.repository.CommentRepository;
import com.openclassrooms.mddapi.post.domain.Post;
import com.openclassrooms.mddapi.post.dto.CreatePostRequest;
import com.openclassrooms.mddapi.post.repository.PostRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.topic.domain.Topic;
import com.openclassrooms.mddapi.topic.repository.TopicRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock private PostRepository postRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private TopicRepository topicRepository;
  @Mock private UserAccountRepository userAccountRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private UserAccount author;
  @Mock private UserAccount postAuthor;
  @Mock private Topic topic;
  @Mock private Post post;

  private PostService postService;

  @BeforeEach
  void setUp() {
    postService =
        new PostService(
            postRepository,
            commentRepository,
            topicRepository,
            userAccountRepository,
            properties(),
            eventPublisher);
  }

  @Test
  void shouldRetrievePostsInTheRequestedDirection() {
    when(postRepository.findAllForSubscribedTopics(12L, Sort.by(Sort.Direction.ASC, "createdAt")))
        .thenReturn(List.of());

    assertThat(postService.getPosts(12L, "asc")).isEmpty();

    verify(postRepository)
        .findAllForSubscribedTopics(12L, Sort.by(Sort.Direction.ASC, "createdAt"));
  }

  @Test
  void shouldRejectAnInvalidPostSortDirection() {
    assertThatThrownBy(() -> postService.getPosts(12L, "invalid"))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.INVALID_REQUEST));
  }

  @Test
  void shouldRejectPostCreationForAnUnknownAuthor() {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> postService.createPost(12L, new CreatePostRequest(34L, "Title", "Content")))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode())
                    .isEqualTo(ApiErrorCode.AUTHENTICATION_REQUIRED));
  }

  @Test
  void shouldRejectPostCreationForAnUnknownTopic() {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.of(author));
    when(topicRepository.findById(34L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> postService.createPost(12L, new CreatePostRequest(34L, "Title", "Content")))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.RESOURCE_NOT_FOUND));
  }

  @Test
  void shouldCreateAPostForAnExistingAuthorAndTopic() {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.of(author));
    when(topicRepository.findById(34L)).thenReturn(Optional.of(topic));

    postService.createPost(12L, new CreatePostRequest(34L, "Title", "Content"));

    verify(postRepository).save(any(Post.class));
  }

  @Test
  void shouldCreateACommentAndNotifyThePostAuthor() {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.of(author));
    when(postRepository.findByIdForSubscribedTopic(56L, 12L)).thenReturn(Optional.of(post));
    when(post.getAuthor()).thenReturn(postAuthor);
    when(postAuthor.getEmail()).thenReturn("author@example.test");
    when(post.getTitle()).thenReturn("Article title");

    postService.createComment(12L, 56L, new CreateCommentRequest("Useful comment."));

    verify(commentRepository).save(any());
    ArgumentCaptor<CommentCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(CommentCreatedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isEqualTo(new CommentCreatedEvent("author@example.test", "Article title"));
  }

  private MddProperties properties() {
    MddProperties properties = new MddProperties();
    properties
        .getMessages()
        .getValidation()
        .setInvalidRequest("The request contains an invalid value.");
    properties.getMessages().getErrors().setAuthenticationRequired("Authentication is required.");
    properties
        .getMessages()
        .getErrors()
        .setResourceNotFound("The requested resource was not found.");
    return properties;
  }
}
