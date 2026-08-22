package com.openclassrooms.mddapi.topic.service;

import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.topic.domain.Subscription;
import com.openclassrooms.mddapi.topic.dto.TopicResponse;
import com.openclassrooms.mddapi.topic.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.topic.repository.TopicRepository;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicService {

  private final TopicRepository topicRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final MddProperties properties;

  public TopicService(
      TopicRepository topicRepository,
      SubscriptionRepository subscriptionRepository,
      MddProperties properties) {
    this.topicRepository = topicRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.properties = properties;
  }

  @Transactional(readOnly = true)
  public List<TopicResponse> getTopics(Long userId) {
    Set<Long> subscribedTopicIds = subscriptionRepository.findTopicIdsByUserId(userId);
    return topicRepository.findAllByOrderByIdAsc().stream()
        .map(topic -> TopicResponse.from(topic, subscribedTopicIds.contains(topic.getId())))
        .toList();
  }

  @Transactional
  public void subscribe(Long userId, Long topicId) {
    if (!topicRepository.existsById(topicId)) {
      throw new ApiException(
          HttpStatus.NOT_FOUND,
          ApiErrorCode.RESOURCE_NOT_FOUND,
          properties.getMessages().getErrors().getResourceNotFound());
    }

    if (!subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
      subscriptionRepository.save(Subscription.subscribe(userId, topicId));
    }
  }
}
