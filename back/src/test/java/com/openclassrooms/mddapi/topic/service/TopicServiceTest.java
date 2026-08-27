package com.openclassrooms.mddapi.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.topic.domain.Subscription;
import com.openclassrooms.mddapi.topic.domain.Topic;
import com.openclassrooms.mddapi.topic.dto.TopicResponse;
import com.openclassrooms.mddapi.topic.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.topic.repository.TopicRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

  @Mock private TopicRepository topicRepository;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private Topic javaTopic;
  @Mock private Topic angularTopic;

  private TopicService topicService;

  @BeforeEach
  void setUp() {
    topicService = new TopicService(topicRepository, subscriptionRepository, properties());
  }

  @Test
  void shouldListTopicsWithTheCurrentSubscriptionStatus() {
    when(subscriptionRepository.findTopicIdsByUserId(12L)).thenReturn(Set.of(1L));
    when(topicRepository.findAllByOrderByIdAsc()).thenReturn(List.of(javaTopic, angularTopic));
    when(javaTopic.getId()).thenReturn(1L);
    when(javaTopic.getName()).thenReturn("Java");
    when(javaTopic.getDescription()).thenReturn("Java topic");
    when(angularTopic.getId()).thenReturn(2L);
    when(angularTopic.getName()).thenReturn("Angular");
    when(angularTopic.getDescription()).thenReturn("Angular topic");

    assertThat(topicService.getTopics(12L))
        .containsExactly(
            new TopicResponse(1L, "Java", "Java topic", true),
            new TopicResponse(2L, "Angular", "Angular topic", false));
  }

  @Test
  void shouldCreateASubscriptionWhenTheTopicExistsAndIsNotAlreadyFollowed() {
    when(topicRepository.existsById(34L)).thenReturn(true);
    when(subscriptionRepository.existsByUserIdAndTopicId(12L, 34L)).thenReturn(false);

    topicService.subscribe(12L, 34L);

    ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
    verify(subscriptionRepository).save(subscriptionCaptor.capture());
    assertThat(subscriptionCaptor.getValue()).isNotNull();
  }

  @Test
  void shouldNotCreateAnotherSubscriptionWhenTheTopicIsAlreadyFollowed() {
    when(topicRepository.existsById(34L)).thenReturn(true);
    when(subscriptionRepository.existsByUserIdAndTopicId(12L, 34L)).thenReturn(true);

    topicService.subscribe(12L, 34L);

    verify(subscriptionRepository, never()).save(any());
  }

  @Test
  void shouldRejectASubscriptionToAnUnknownTopic() {
    when(topicRepository.existsById(34L)).thenReturn(false);

    assertThatThrownBy(() -> topicService.subscribe(12L, 34L))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.RESOURCE_NOT_FOUND));

    verify(subscriptionRepository, never()).existsByUserIdAndTopicId(any(), any());
  }

  @Test
  void shouldRemoveASubscription() {
    topicService.unsubscribe(12L, 34L);

    verify(subscriptionRepository).deleteByUserIdAndTopicId(12L, 34L);
  }

  private MddProperties properties() {
    MddProperties properties = new MddProperties();
    properties
        .getMessages()
        .getErrors()
        .setResourceNotFound("The requested resource was not found.");
    return properties;
  }
}
