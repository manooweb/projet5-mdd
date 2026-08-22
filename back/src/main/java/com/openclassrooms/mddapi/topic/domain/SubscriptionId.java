package com.openclassrooms.mddapi.topic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubscriptionId implements Serializable {

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "topic_id")
  private Long topicId;

  protected SubscriptionId() {}

  public SubscriptionId(Long userId, Long topicId) {
    this.userId = userId;
    this.topicId = topicId;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getTopicId() {
    return topicId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof SubscriptionId that)) {
      return false;
    }
    return Objects.equals(userId, that.userId) && Objects.equals(topicId, that.topicId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, topicId);
  }
}
