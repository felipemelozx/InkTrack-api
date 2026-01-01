package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.domain.User;
import com.inktrack.core.exception.FieldDomainValidationException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReadingSessionUseCaseImplTest {
  @Mock
  private ReadingSessionGateway readingSessionGateway;
  @Mock
  private BookGateway bookGateway;

  @InjectMocks
  private CreateReadingSessionUseCaseImpl createReadingSessionUseCase;

  private Book validBook;
  private User validUser;


  @BeforeEach
  void setUp() {
    validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
    OffsetDateTime sevenDaysBeforeToday = OffsetDateTime.now().minusDays(7);
    validBook = Book.builder()
        .id(1l)
        .user(validUser)
        .pagesRead(0)
        .totalPages(100)
        .author("some author")
        .title("Some title")
        .createdAt(sevenDaysBeforeToday)
        .updatedAt(sevenDaysBeforeToday)
        .build();
  }


  @Test
  @DisplayName("Should create a reading session and update book pages successfully")
  void shouldCreateReadingSessionSuccessfully() {
    // Arrange
    UUID userId = validUser.getId();
    ReadingSessionInput input = new ReadingSessionInput(1L, 20L, 20);

    ReadingSession savedSession = ReadingSession.builder()
        .id(10L)
        .book(validBook)
        .minutes(20L)
        .pagesRead(20)
        .sessionDate(OffsetDateTime.now())
        .build();

    when(bookGateway.findByIdAndUserId(1L, userId))
        .thenReturn(validBook);

    when(readingSessionGateway.save(any(ReadingSession.class)))
        .thenReturn(savedSession);

    // Act
    ReadingSessionOutput output =
        createReadingSessionUseCase.execute(input, userId);

    // Assert
    assertNotNull(output);
    assertEquals(10L, output.id());
    assertEquals(1L, output.bookId());
    assertEquals(20L, output.minutes());
    assertEquals(20, output.pagesRead());
    assertNotNull(output.sessionDate());

    assertEquals(20, validBook.getPagesRead());

    verify(bookGateway).findByIdAndUserId(1L, userId);
    verify(bookGateway).update(validBook);
    verify(readingSessionGateway).save(any(ReadingSession.class));
  }

  @Test
  @DisplayName("Should throw FieldDomainValidationException when userId is null")
  void shouldThrowExceptionWhenUserIdIsNull() {
    // Arrange
    ReadingSessionInput input = new ReadingSessionInput(1L, 20L, 20);

    // Act & Assert
    FieldDomainValidationException exception =
        assertThrows(
            FieldDomainValidationException.class,
            () -> createReadingSessionUseCase.execute(input, null)
        );

    assertEquals("userId", exception.getFieldName());
    assertEquals("The user id can't be null.", exception.getMessage());
  }

  @Test
  @DisplayName("Should increase book pages read after creating reading session")
  void shouldIncreaseBookPagesRead() {
    // Arrange
    UUID userId = validUser.getId();
    ReadingSessionInput input = new ReadingSessionInput(1L, 15L, 15);

    when(bookGateway.findByIdAndUserId(1L, userId))
        .thenReturn(validBook);

    when(readingSessionGateway.save(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    createReadingSessionUseCase.execute(input, userId);

    // Assert
    assertEquals(15, validBook.getPagesRead());
  }

}
