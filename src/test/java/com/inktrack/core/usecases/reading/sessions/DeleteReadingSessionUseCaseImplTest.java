package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.domain.User;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.ReadingSessionGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteReadingSessionUseCaseImplTest {

  @Mock
  private ReadingSessionGateway readingSessionGateway;

  @Mock
  private BookGateway bookGateway;

  @InjectMocks
  private DeleteReadingSessionUseCaseImpl deleteReadingSessionUseCase;

  private Book validBook;
  private User validUser;

  @BeforeEach
  void setUp() {
    validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
    OffsetDateTime sevenDaysBeforeToday = OffsetDateTime.now().minusDays(7);
    validBook = Book.builder()
        .id(1L)
        .user(validUser)
        .category(new Category(1L, "Fiction", OffsetDateTime.now()))
        .pagesRead(20)
        .totalPages(100)
        .author("some author")
        .title("Some title")
        .createdAt(sevenDaysBeforeToday)
        .updatedAt(sevenDaysBeforeToday)
        .build();
  }

  @Test
  @DisplayName("Should delete a reading session successfully")
  void shouldDeleteReadingSessionSuccessfully() {
    UUID userId = validUser.getId();
    Long readingSessionId = 1L;
    when(bookGateway.findByIdAndUserId(validBook.getId(), validUser.getId())).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, validBook.getId(), userId))
        .thenReturn(Optional.ofNullable(
            ReadingSession.builder()
                .id(readingSessionId)
                .book(validBook)
                .minutes(12L)
                .pagesRead(20)
                .sessionDate(OffsetDateTime.now())
                .build()
        ));
    when(readingSessionGateway.deleteReadingSession(readingSessionId, userId, validBook.getId()))
        .thenReturn(1);
    when(bookGateway.update(any(Book.class))).thenReturn(validBook);

    deleteReadingSessionUseCase.execute(readingSessionId, userId, validBook.getId());

    verify(readingSessionGateway).deleteReadingSession(readingSessionId, userId, validBook.getId());
    verify(bookGateway).findByIdAndUserId(validBook.getId(), userId);
    verify(bookGateway).update(validBook);
  }

  @Test
  @DisplayName("Should throw Resource not found exception when the reading session does not exist")
  void shouldThrowResourceNotFoundExceptionWhenReadingSessionDoesNotExist() {
    UUID userId = validUser.getId();
    Long readingSessionId = 1L;
    when(bookGateway.findByIdAndUserId(validBook.getId(), validUser.getId())).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, validBook.getId(), userId))
        .thenReturn(Optional.empty());

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> deleteReadingSessionUseCase.execute(readingSessionId, userId, validBook.getId()));

    String message = String.format(
        "ReadingSession not found with sessionId=%d, bookId=%d, userId=%s",
        readingSessionId, validBook.getId(), userId
    );
    assertEquals(message, ex.getMessage());
    assertEquals("ReadingSession", ex.getResource());
    assertEquals("compositeId", ex.getField());
  }

  @Test
  @DisplayName("Should throw IllegalStateException when deletion fails")
  void shouldThrowIllegalStateExceptionWhenDeletionFails() {
    UUID userId = validUser.getId();
    Long readingSessionId = 1L;
    when(bookGateway.findByIdAndUserId(validBook.getId(), validUser.getId())).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, validBook.getId(), userId))
        .thenReturn(Optional.ofNullable(
            ReadingSession.builder()
                .id(readingSessionId)
                .book(validBook)
                .minutes(12L)
                .pagesRead(20)
                .sessionDate(OffsetDateTime.now())
                .build()
        ));
    when(readingSessionGateway.deleteReadingSession(readingSessionId, userId, validBook.getId()))
        .thenReturn(0);

    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> deleteReadingSessionUseCase.execute(readingSessionId, userId, validBook.getId()));
    String message = "ReadingSession was found but could not be deleted. Possible concurrent modification.";
    assertEquals(message, ex.getMessage());
  }
}
