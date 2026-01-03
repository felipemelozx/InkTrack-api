package com.inktrack.core.domain;

import com.inktrack.core.exception.FieldDomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

  private Book createTestBook() {
    User user = new User(java.util.UUID.randomUUID(), "User", "user@email.com", "pass", java.time.LocalDateTime.now());
    return Book.builder()
        .title("Title")
        .author("Author")
        .totalPages(100)
        .user(user)
        .build();
  }

  @Test
  @DisplayName("Should create note when data is valid")
  void shouldCreateNoteWhenDataIsValid() {
    Book book = createTestBook();
    OffsetDateTime now = OffsetDateTime.now();
    Note note = new Note(1L, book, "Some content", now, now);

    assertEquals(1L, note.getId());
    assertEquals(book, note.getBook());
    assertEquals("Some content", note.getContent());
    assertEquals(now, note.getCreatedAt());
    assertEquals(now, note.getUpdatedAt());
  }

  @Test
  @DisplayName("Should throw exception when content is blank")
  void shouldThrowExceptionWhenContentIsBlank() {
    Book book = createTestBook();
    assertThrows(FieldDomainValidationException.class, () -> new Note(book, "   ", null, null));
    assertThrows(FieldDomainValidationException.class, () -> new Note(book, "", null, null));
  }

  @Test
  @DisplayName("Should throw exception when content length exceeds 255")
  void shouldThrowExceptionWhenContentIsTooLong() {
    Book book = createTestBook();
    String longContent = "a".repeat(256);
    assertThrows(FieldDomainValidationException.class, () -> new Note(book, longContent, null, null));
  }

  @Test
  @DisplayName("Should throw exception when book is null")
  void shouldThrowExceptionWhenBookIsNull() {
    assertThrows(FieldDomainValidationException.class, () -> new Note(null, "content", null, null));
  }
}
