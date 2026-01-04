package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.utils.PageResult;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReadingSessionByBookIdUseCaseImplTest {

  @Mock
  private ReadingSessionGateway readingSessionGateway;

  @InjectMocks
  private GetReadingSessionByBookIdUseCaseImpl getReadingSessionByBookIdUseCase;

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
        .pagesRead(0)
        .totalPages(100)
        .author("some author")
        .title("Some title")
        .createdAt(sevenDaysBeforeToday)
        .updatedAt(sevenDaysBeforeToday)
        .build();
  }

  @Test
  @DisplayName("Should return the reading sessions by book ID and user ID successfully paginated")
  void shouldReturnReadingSessionsByBookIdAndUserIdSuccessfullyPaginated() {
    // arrange
    ReadingSession readingSession1 = ReadingSession.builder()
        .id(1L)
        .book(validBook)
        .minutes(30L)
        .pagesRead(20)
        .sessionDate(OffsetDateTime.now())
        .build();
    ReadingSession readingSession2 = ReadingSession.builder()
        .id(1L)
        .book(validBook)
        .minutes(30L)
        .pagesRead(20)
        .sessionDate(OffsetDateTime.now())
        .build();
    ReadingSession readingSession3 = ReadingSession.builder()
        .id(1L)
        .book(validBook)
        .minutes(30L)
        .pagesRead(20)
        .sessionDate(OffsetDateTime.now())
        .build();

    when(readingSessionGateway.getReadingByBookIdAndUserId(validBook.getId(), validUser.getId(), 0, 3))
        .thenReturn(new PageResult<>(
            3,
            1,
            0,
            java.util.List.of(readingSession1, readingSession2, readingSession3)
        ));


    PageResult<ReadingSessionOutput> result = getReadingSessionByBookIdUseCase
        .execute(validBook.getId(), validUser.getId(), 0);

    assertNotNull(result);
    assertEquals(3, result.pageSize());
    assertEquals(1, result.totalPages());
    assertEquals(0, result.currentPage());
    assertEquals(3, result.data().size());
  }
}
