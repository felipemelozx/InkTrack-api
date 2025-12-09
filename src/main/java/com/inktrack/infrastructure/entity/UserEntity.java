package com.inktrack.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(length = 50, nullable = false, unique = true)
  private String email;

  @Column(length = 255, nullable = false)
  private String password;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  public UserEntity() {
  }

  public UserEntity(UUID id, String name, String email, String password) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
  }


  public UserEntity(UUID id, String name, String email, String password, LocalDateTime createdAt) {
   this(id, name, email, password);
   this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, password);
  }
}
