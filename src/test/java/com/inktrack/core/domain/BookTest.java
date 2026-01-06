package com.inktrack.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {

  private User createTestUser() {
    return new User(java.util.UUID.randomUUID(), "Test User", "test@email.com", "password",
        java.time.LocalDateTime.now());
  }

  private Category createTestCategory() {
    return new Category(1L, "FICTION", OffsetDateTime.now());
  }

  @Test
  @DisplayName("Should create book when data is valid")
  void shouldCreateBookWhenDataIsValid() {
    User user = createTestUser();
    Category category = createTestCategory();
    Book book = Book.builder()
        .title("Title")
        .author("Author")
        .totalPages(100)
        .user(user)
        .category(category)
        .build();

    assertEquals("Title", book.getTitle());
    assertEquals("Author", book.getAuthor());
    assertEquals(100, book.getTotalPages());
    assertEquals(0, book.getPagesRead());
    assertEquals(user, book.getUser());
    assertEquals(category, book.getCategory());
  }

  @Test
  @DisplayName("Should throw exception when totalPages is zero or negative")
  void shouldThrowExceptionWhenTotalPagesIsInvalid() {
    assertThrows(IllegalArgumentException.class, () -> createBookWithTotalPages(0));
    assertThrows(IllegalArgumentException.class, () -> createBookWithTotalPages(-1));
  }

  private Book createBookWithTotalPages(int totalPages) {
    return Book.builder()
        .totalPages(totalPages)
        .user(createTestUser())
        .title("T")
        .author("A")
        .category(createTestCategory())
        .build();
  }

  @Test
  @DisplayName("Should throw exception when pagesRead is negative or greater than totalPages")
  void shouldThrowExceptionWhenPagesReadIsInvalid() {
    assertThrows(IllegalArgumentException.class, () -> createBookWithPagesRead(-1));
    assertThrows(IllegalArgumentException.class, () -> createBookWithPagesRead(101));
  }

  private Book createBookWithPagesRead(int pagesRead) {
    return Book.builder()
        .totalPages(100)
        .pagesRead(pagesRead)
        .user(createTestUser())
        .title("T")
        .author("A")
        .category(createTestCategory())
        .build();
  }

  @Test
  @DisplayName("Should throw exception when user, title or author is null")
  void shouldThrowExceptionWhenRequiredFieldsAreNull() {
    assertThrows(NullPointerException.class, this::createBookWithNullUser);
    assertThrows(NullPointerException.class, this::createBookWithNullTitle);
    assertThrows(NullPointerException.class, this::createBookWithNullAuthor);
    assertThrows(NullPointerException.class, this::createBookWithNullCategory);
  }

  private Book createBookWithNullUser() {
    return Book.builder()
        .totalPages(100)
        .user(null)
        .title("T")
        .author("A")
        .category(createTestCategory())
        .build();
  }

  private Book createBookWithNullTitle() {
    return Book.builder()
        .totalPages(100)
        .user(createTestUser())
        .title(null)
        .author("A")
        .category(createTestCategory())
        .build();
  }

  private Book createBookWithNullAuthor() {
    return Book.builder()
        .totalPages(100)
        .user(createTestUser())
        .title("T")
        .author(null)
        .category(createTestCategory())
        .build();
  }

  private Book createBookWithNullCategory() {
    return Book.builder()
        .totalPages(100)
        .user(createTestUser())
        .title("T")
        .author("A")
        .category(null)
        .build();
  }

  @Test
  @DisplayName("Should add pages read successfully")
  void shouldAddPagesRead() {
    Book book = Book.builder().totalPages(100).user(createTestUser()).title("T").author("A").category(createTestCategory()).build();
    book.addPagesRead(10);
    assertEquals(10, book.getPagesRead());

    book.addPagesRead(90);
    assertEquals(100, book.getPagesRead());
  }

  @Test
  @DisplayName("Should throw exception when adding negative pages or exceeding total")
  void shouldThrowExceptionWhenAddingInvalidPages() {
    Book book = Book.builder().totalPages(100).pagesRead(90).user(createTestUser()).title("T").author("A").category(createTestCategory()).build();
    assertThrows(IllegalArgumentException.class, () -> book.addPagesRead(-1));
    assertThrows(IllegalArgumentException.class, () -> book.addPagesRead(11));
  }

  @Test
  @DisplayName("Should remove pages read successfully")
  void shouldRemovePagesRead() {
    Book book = Book.builder().totalPages(100).pagesRead(50).user(createTestUser()).title("T").author("A").category(createTestCategory()).build();
    book.removePagesRead(10);
    assertEquals(40, book.getPagesRead());

    book.removePagesRead(40);
    assertEquals(0, book.getPagesRead());
  }

  @Test
  @DisplayName("Should throw exception when removing negative pages or resulting in negative pagesRead")
  void shouldThrowExceptionWhenRemovingInvalidPages() {
    Book book = Book.builder().totalPages(100).pagesRead(10).user(createTestUser()).title("T").author("A").category(createTestCategory()).build();
    assertThrows(IllegalArgumentException.class, () -> book.removePagesRead(-1));
    assertThrows(IllegalArgumentException.class, () -> book.removePagesRead(11));
  }

  @Test
  @DisplayName("Should calculate progress correctly")
  void shouldCalculateProgress() {
    Book book = Book.builder().totalPages(200).pagesRead(50).user(createTestUser()).title("T").author("A").category(createTestCategory()).build();
    assertEquals(25, book.getProgress());

    book.addPagesRead(50);
    assertEquals(50, book.getProgress());
  }
}
