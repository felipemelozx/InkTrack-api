package com.inktrack.core.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class Book {


  private final Long id;
  private final User user;
  private final String title;
  private final String author;
  private final int totalPages;
  private int pagesRead;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  private Book(Builder builder) {
    validate(builder);

    this.id = builder.id;
    this.user = builder.user;
    this.title = builder.title;
    this.author = builder.author;
    this.totalPages = builder.totalPages;
    this.pagesRead = builder.pagesRead;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  private void validate(Builder builder) {
    if (builder.totalPages <= 0) {
      throw new IllegalArgumentException("totalPages must be greater than zero");
    }
    if (builder.pagesRead < 0 || builder.pagesRead > builder.totalPages) {
      throw new IllegalArgumentException("pagesRead must be between 0 and totalPages");
    }

    Objects.requireNonNull(builder.user, "user must not be null");
    Objects.requireNonNull(builder.title, "title must not be null");
    Objects.requireNonNull(builder.author, "author must not be null");
  }


  public void updatePagesRead(int newValue) {
    if (newValue < 0 || newValue > totalPages) {
      throw new IllegalArgumentException("pagesRead must be between 0 and totalPages");
    }
    this.pagesRead = newValue;
    this.updatedAt = OffsetDateTime.now();
  }

  public int getProgress() {
    return (pagesRead * 100) / totalPages;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public int getPagesRead() {
    return pagesRead;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Long id;
    private User user;
    private String title;
    private String author;
    private int totalPages;
    private int pagesRead = 0;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder user(User user) {
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

    public Builder totalPages(int totalPages) {
      this.totalPages = totalPages;
      return this;
    }

    public Builder pagesRead(int pagesRead) {
      this.pagesRead = pagesRead;
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

    public Book build() {
      return new Book(this);
    }
  }
}
