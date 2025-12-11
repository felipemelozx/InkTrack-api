package com.inktrack.core.domain;

import java.time.OffsetDateTime;

public class  Book {
  private Long id;
  private User user;
  private String title;
  private String author;
  private int totalPages;
  private int pagesRead;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public Book(
      Long id,
      User user,
      String title,
      String author,
      int totalPages,
      int pagesRead,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt
  ) {
    if (totalPages <= 0) {
      throw new IllegalArgumentException("totalPages must be greater than zero");
    }
    if (pagesRead < 0 || pagesRead > totalPages) {
      throw new IllegalArgumentException("pagesRead must be between 0 and totalPages");
    }

    this.id = id;
    this.user = user;
    this.title = title;
    this.author = author;
    this.totalPages = totalPages;
    this.pagesRead = pagesRead;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }


  public Book(
      User user,
      String title,
      String author,
      int totalPages,
      int pagesRead
  ) {
    if (totalPages <= 0) {
      throw new IllegalArgumentException("totalPages must be greater than zero");
    }
    if (pagesRead < 0 || pagesRead > totalPages) {
      throw new IllegalArgumentException("pagesRead must be between 0 and totalPages");
    }

    this.id = null;
    this.user = user;
    this.title = title;
    this.author = author;
    this.totalPages = totalPages;
    this.pagesRead = pagesRead;
    this.createdAt = null;
    this.updatedAt = null;
  }

  public void updatePagesRead(int newValue) {
    if (newValue < 0 || newValue > totalPages) {
      throw new IllegalArgumentException("pagesRead must be between 0 and totalPages");
    }
    this.pagesRead = newValue;
    this.updatedAt = OffsetDateTime.now();
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
}
