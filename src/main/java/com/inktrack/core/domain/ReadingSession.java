package com.inktrack.core.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class ReadingSession {

  private Long id;
  private Book book;
  private Long minutes;
  private Integer pagesRead;
  private OffsetDateTime sessionDate;

  private ReadingSession(Builder builder) {
    this.id = builder.id;
    this.book = builder.book;
    this.minutes = builder.minutes;
    this.pagesRead = builder.pagesRead;
    this.sessionDate = builder.sessionDate;
  }

  public Long getId() {
    return id;
  }

  public Book getBook() {
    return book;
  }

  public Long getMinutes() {
    return minutes;
  }

  public Integer getPagesRead() {
    return pagesRead;
  }

  public OffsetDateTime getSessionDate() {
    return sessionDate;
  }

  public static class Builder {

    private Long id;
    private Book book;
    private Long minutes;
    private Integer pagesRead;
    private OffsetDateTime sessionDate;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder book(Book book) {
      this.book = book;
      return this;
    }

    public Builder minutes(Long minutes) {
      this.minutes = minutes;
      return this;
    }

    public Builder pagesRead(Integer pagesRead) {
      this.pagesRead = pagesRead;
      return this;
    }

    public Builder sessionDate(OffsetDateTime sessionDate) {
      this.sessionDate = sessionDate;
      return this;
    }

    public ReadingSession build() {
      validate();
      return new ReadingSession(this);
    }

    private void validate() {
      Objects.requireNonNull(book, "bookId must not be null");
      Objects.requireNonNull(minutes, "minutes must not be null");
      Objects.requireNonNull(pagesRead, "pagesRead must not be null");
      Objects.requireNonNull(sessionDate, "sessionDate must not be null");

      if (pagesRead <= 0) {
        throw new IllegalArgumentException("pagesRead must be greater than zero");
      }

      if (minutes <= 0) {
        throw new IllegalArgumentException("minutes must be greater than zero");
      }
    }
  }

  public static Builder builder() {
    return new Builder();
  }

}
