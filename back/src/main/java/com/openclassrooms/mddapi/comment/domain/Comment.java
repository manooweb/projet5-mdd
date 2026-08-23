package com.openclassrooms.mddapi.comment.domain;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.post.domain.Post;
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
@Table(name = "comments")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount author;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(nullable = false)
  private Instant createdAt;

  protected Comment() {}

  public Long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }

  public UserAccount getAuthor() {
    return author;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
