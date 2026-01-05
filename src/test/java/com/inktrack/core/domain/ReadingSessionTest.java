package com.inktrack.core.domain;

import com.inktrack.core.exception.FieldDomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReadingSessionTest {

  private Book createTestBook() {
    User user = new User(java.util.UUID.randomUUID(), "User", "user@email.com", "pass", java.time.LocalDateTime.now());
    Category category = new Category(1L, "Fiction", OffsetDateTime.now());
    return Book.builder()
        .title("Title")
        .author("Author")
        .totalPages(100)
        .user(user)
        .category(category)
        .build();
  }

  @Test
  @DisplayName("Should create reading session when data is valid")
  void shouldCreateReadingSessionWhenDataIsValid() {
    Book book = createTestBook();
    OffsetDateTime now = OffsetDateTime.now();
    ReadingSession session = ReadingSession.builder()
        .id(1L)
        .book(book)
        .minutes(30L)
        .pagesRead(20)
        .sessionDate(now)
        .build();

    assertEquals(1L, session.getId());
    assertEquals(book, session.getBook());
    assertEquals(30L, session.getMinutes());
    assertEquals(20, session.getPagesRead());
    assertEquals(now, session.getSessionDate());
  }

  @Test
  @DisplayName("Should throw exception when required fields are null")
  void shouldThrowExceptionWhenFieldsAreNull() {
    assertThrows(NullPointerException.class, () -> ReadingSession.builder().build());
    assertThrows(NullPointerException.class, () -> ReadingSession.builder().book(createTestBook()).build());
    assertThrows(NullPointerException.class,
        () -> ReadingSession.builder().book(createTestBook()).minutes(30L).build());
    assertThrows(NullPointerException.class,
        () -> ReadingSession.builder().book(createTestBook()).minutes(30L).pagesRead(10).build());
  }

  @Test
  @DisplayName("Should throw exception when pagesRead or minutes are zero or negative")
  void shouldThrowExceptionWhenValuesAreInvalid() {
    Book book = createTestBook();
    OffsetDateTime now = OffsetDateTime.now();

    assertThrows(FieldDomainValidationException.class, () -> ReadingSession.builder()
        .book(book).minutes(30L).pagesRead(0).sessionDate(now).build());
    assertThrows(FieldDomainValidationException.class, () -> ReadingSession.builder()
        .book(book).minutes(30L).pagesRead(-1).sessionDate(now).build());
    assertThrows(FieldDomainValidationException.class, () -> ReadingSession.builder()
        .book(book).minutes(0L).pagesRead(10).sessionDate(now).build());
    assertThrows(FieldDomainValidationException.class, () -> ReadingSession.builder()
        .book(book).minutes(-1L).pagesRead(10).sessionDate(now).build());
  }
}
