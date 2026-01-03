package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
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

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateReadingSessionUseCaseImplTest {

  @Mock
  private ReadingSessionGateway readingSessionGateway;
  @Mock
  private BookGateway bookGateway;
  @InjectMocks
  private UpdateReadingSessionUseCaseImpl updateReadingSessionUseCase;

  private Book validBook;
  private ReadingSession validReadingSession;
  private ReadingSessionInput validInput;
  private UUID userId;
  private Long bookId;
  private Long readingSessionId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    bookId = 1L;
    readingSessionId = 1L;

    validBook = Book.builder()
        .id(bookId)
        .title("Test Book")
        .pagesRead(50)
        .author("Test Author")
        .user(new User(userId, "Test User", "test@test.com", "password123", null))
        .totalPages(200)
        .build();

    validReadingSession = ReadingSession.builder()
        .id(readingSessionId)
        .book(validBook)
        .pagesRead(10)
        .minutes(30L)
        .sessionDate(OffsetDateTime.now())
        .build();

    validInput = new ReadingSessionInput(bookId, 45L, 15);
  }

  @Test
  @DisplayName("Should update reading session successfully")
  void shouldUpdateReadingSessionSuccessfully() {
    when(bookGateway.findByIdAndUserId(bookId, userId)).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId))
        .thenReturn(Optional.of(validReadingSession));

    ReadingSessionOutput result = updateReadingSessionUseCase.execute(bookId, userId, readingSessionId, validInput);

    assertNotNull(result);
    assertEquals(readingSessionId, result.id());
    assertEquals(bookId, result.bookId());
    assertEquals(15, result.pagesRead());
    assertEquals(45, result.minutes());

    verify(bookGateway).findByIdAndUserId(bookId, userId);
    verify(readingSessionGateway).getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId);
    verify(bookGateway).update(any(Book.class));
    verify(readingSessionGateway).update(any(ReadingSession.class));
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when reading session not found")
  void shouldThrowResourceNotFoundExceptionWhenReadingSessionNotFound() {
    when(bookGateway.findByIdAndUserId(bookId, userId)).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        updateReadingSessionUseCase.execute(bookId, userId, readingSessionId, validInput));

    verify(bookGateway, never()).update(any(Book.class));
    verify(readingSessionGateway, never()).update(any(ReadingSession.class));
  }

  @Test
  @DisplayName("Should update book pages read correctly")
  void shouldUpdateBookPagesReadCorrectly() {
    when(bookGateway.findByIdAndUserId(bookId, userId)).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId))
        .thenReturn(Optional.of(validReadingSession));

    updateReadingSessionUseCase.execute(bookId, userId, readingSessionId, validInput);

    assertEquals(55, validBook.getPagesRead());
    verify(bookGateway).update(validBook);
  }

  @Test
  @DisplayName("Should create updated reading session with new input values")
  void shouldCreateUpdatedReadingSessionWithNewValues() {
    when(bookGateway.findByIdAndUserId(bookId, userId)).thenReturn(validBook);
    when(readingSessionGateway.getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId))
        .thenReturn(Optional.of(validReadingSession));

    updateReadingSessionUseCase.execute(bookId, userId, readingSessionId, validInput);

    verify(readingSessionGateway).update(argThat(session ->
        session.getId().equals(readingSessionId) &&
            session.getPagesRead() == 15 &&
            session.getMinutes() == 45
    ));
  }
}
