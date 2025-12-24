package com.inktrack.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;


@Entity
@Table(name = "tb_books")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(name = "total_pages", nullable = false)
  private Integer totalPages;

  @Column(name = "pages_read", nullable = false)
  private Integer pagesRead;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private Integer progress;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;


  protected BookEntity() {
  }

  private BookEntity(Builder builder) {
    this.id = builder.id;
    this.user = builder.user;
    this.title = builder.title;
    this.author = builder.author;
    this.totalPages = builder.totalPages;
    this.pagesRead = builder.pagesRead;
    this.progress = builder.progress;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = OffsetDateTime.now();
    this.updatedAt = OffsetDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Long id;
    private UserEntity user;
    private String title;
    private String author;
    private Integer totalPages;
    private Integer pagesRead;
    private Integer progress;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Builder() {}

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder user(UserEntity user) {
      this.user = user;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder totalPages(Integer totalPages) {
      this.totalPages = totalPages;
      return this;
    }

    public Builder pagesRead(Integer pagesRead) {
      this.pagesRead = pagesRead;
      return this;
    }

    public Builder progress(Integer progress) {
      this.progress = progress;
      return this;
    }

    public Builder createdAt(OffsetDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(OffsetDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public BookEntity build() {
      if (user == null || title == null || author == null || totalPages <= 0) {
        throw new IllegalStateException("User, title and author are required");
      }

      if (progress != null && (progress < 0 || progress > 100)) {
        throw new IllegalStateException("Progress must be between 0 and 100");
      }

      return new BookEntity(this);
    }
  }

  public Long getId() {
    return id;
  }

  public UserEntity getUser() {
    return user;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public Integer getPagesRead() {
    return pagesRead;
  }

  public Integer getProgress() {
    return progress;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}