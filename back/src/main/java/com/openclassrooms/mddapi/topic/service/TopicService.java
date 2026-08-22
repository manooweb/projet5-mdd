package com.openclassrooms.mddapi.topic.service;

import com.openclassrooms.mddapi.topic.dto.TopicResponse;
import com.openclassrooms.mddapi.topic.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.topic.repository.TopicRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicService {

  private final TopicRepository topicRepository;
  private final SubscriptionRepository subscriptionRepository;

  public TopicService(
      TopicRepository topicRepository, SubscriptionRepository subscriptionRepository) {
    this.topicRepository = topicRepository;
    this.subscriptionRepository = subscriptionRepository;
  }

  @Transactional(readOnly = true)
  public List<TopicResponse> getTopics(Long userId) {
    Set<Long> subscribedTopicIds = subscriptionRepository.findTopicIdsByUserId(userId);
    return topicRepository.findAllByOrderByIdAsc().stream()
        .map(topic -> TopicResponse.from(topic, subscribedTopicIds.contains(topic.getId())))
        .toList();
  }
}
