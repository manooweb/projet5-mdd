package com.openclassrooms.mddapi.topic.repository;

import com.openclassrooms.mddapi.topic.domain.Subscription;
import com.openclassrooms.mddapi.topic.domain.SubscriptionId;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {

  @Query(
      """
      select subscription.id.topicId
      from Subscription subscription
      where subscription.id.userId = :userId
      """)
  Set<Long> findTopicIdsByUserId(@Param("userId") Long userId);

  @Query(
      """
      select count(subscription) > 0
      from Subscription subscription
      where subscription.id.userId = :userId and subscription.id.topicId = :topicId
      """)
  boolean existsByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);
}
