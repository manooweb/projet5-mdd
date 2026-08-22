package com.openclassrooms.mddapi.topic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class Subscription {

  @EmbeddedId private SubscriptionId id;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected Subscription() {}
}
