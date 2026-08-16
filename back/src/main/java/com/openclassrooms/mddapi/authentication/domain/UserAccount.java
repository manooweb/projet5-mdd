package com.openclassrooms.mddapi.authentication.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "users")
public class UserAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected UserAccount() {}

  private UserAccount(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public static UserAccount register(String username, String email, String password) {
    return new UserAccount(username, email, password);
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void initializeTimestamps() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }
}
