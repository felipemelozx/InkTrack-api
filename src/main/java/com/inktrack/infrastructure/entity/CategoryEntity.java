package com.inktrack.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_categories")
public final class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  private CategoryEntity() {
  }

  public CategoryEntity(Long id, String name, OffsetDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
