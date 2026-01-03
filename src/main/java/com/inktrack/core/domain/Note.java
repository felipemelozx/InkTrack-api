package com.inktrack.core.domain;

import com.inktrack.core.exception.FieldDomainValidationException;

import java.time.OffsetDateTime;

public final class Note {
  private final Long id;
  private final Book book;
  private final String content;
  private final OffsetDateTime createdAt;
  private final OffsetDateTime updatedAt;

  public Note(Long id, Book book, String content, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    if (content.isBlank()) {
      throw new FieldDomainValidationException("content", "The content can't be blank.");
    }
    if (content.length() > 255) {
      throw new FieldDomainValidationException("content", "The content can't be greater than 255 characters.");
    }
    if (book == null) {
      throw new FieldDomainValidationException("bookId", "The book id can't be null.");
    }
    this.id = id;
    this.book = book;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Note(Book book, String content, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this(null, book, content, createdAt, updatedAt);
  }

  public Long getId() {
    return id;
  }

  public Book getBook() {
    return book;
  }

  public String getContent() {
    return content;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
