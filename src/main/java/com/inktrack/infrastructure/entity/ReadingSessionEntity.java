package com.inktrack.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_reading_sessions")
public class ReadingSessionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private BookEntity book;

  @Column(name = "pages_read", nullable = false)
  private Integer pagesRead;

  @Column(name = "minutes", nullable = false)
  private Long minutes;

  @Column(name = "session_date", nullable = false)
  private OffsetDateTime sessionDate;

  protected ReadingSessionEntity() {
  }

  public ReadingSessionEntity(BookEntity book, Integer pagesRead, Long minutes, OffsetDateTime sessionDate) {
    this.book = book;
    this.pagesRead = pagesRead;
    this.minutes = minutes;
    this.sessionDate = sessionDate;
  }

  public Long getId() {
    return id;
  }

  public BookEntity getBook() {
    return book;
  }

  public Integer getPagesRead() {
    return pagesRead;
  }

  public Long getMinutes() {
    return minutes;
  }

  public OffsetDateTime getSessionDate() {
    return sessionDate;
  }
}