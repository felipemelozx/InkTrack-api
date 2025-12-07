package com.inktrack.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

  private UUID id;
  private String name;
  private String email;
  private String password;
  private LocalDateTime createdAt;

  public User(UUID id, String name, String email, String password, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

}
