package com.openclassrooms.mddapi.topic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubscriptionIdTest {

  @Test
  void shouldExposeTheUserAndTopicIdentifiers() {
    SubscriptionId subscriptionId = new SubscriptionId(12L, 34L);

    assertThat(subscriptionId.getUserId()).isEqualTo(12L);
    assertThat(subscriptionId.getTopicId()).isEqualTo(34L);
  }

  @Test
  void shouldConsiderIdentifiersWithTheSameValuesAsEqual() {
    SubscriptionId subscriptionId = new SubscriptionId(12L, 34L);
    SubscriptionId sameSubscriptionId = new SubscriptionId(12L, 34L);

    assertThat(subscriptionId)
        .isEqualTo(subscriptionId)
        .isEqualTo(sameSubscriptionId)
        .hasSameHashCodeAs(sameSubscriptionId);
  }

  @Test
  void shouldNotConsiderDifferentIdentifiersOrOtherObjectsAsEqual() {
    SubscriptionId subscriptionId = new SubscriptionId(12L, 34L);

    assertThat(subscriptionId)
        .isNotEqualTo(null)
        .isNotEqualTo("subscription")
        .isNotEqualTo(new SubscriptionId(56L, 34L))
        .isNotEqualTo(new SubscriptionId(12L, 78L));
  }
}
