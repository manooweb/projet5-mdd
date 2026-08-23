package com.openclassrooms.mddapi.post.domain;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.topic.domain.Topic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount author;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "topic_id", nullable = false)
  private Topic topic;

  @Column(nullable = false)
  private Instant createdAt;

  protected Post() {}

  private Post(UserAccount author, Topic topic, String title, String content) {
    this.author = author;
    this.topic = topic;
    this.title = title;
    this.content = content;
    createdAt = Instant.now();
  }

  public static Post create(UserAccount author, Topic topic, String title, String content) {
    return new Post(author, topic, title, content);
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public UserAccount getAuthor() {
    return author;
  }

  public Topic getTopic() {
    return topic;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
