package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBookUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  private UpdateBookUseCase updateBookUseCase;

  private User validUser;

  @BeforeEach
  void setUp() {
    updateBookUseCase = new UpdateBookUseCaseImpl(bookGateway);
    validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
  }

  @Test
  @DisplayName("Should update book successfully when all data is valid")
  void execute_shouldUpdateBook_whenDataIsValid() {
    OffsetDateTime sevenDaysBeforeToday = OffsetDateTime.now().minusDays(7);
    Book bookSaved = Book.builder()
        .id(1L)
        .user(validUser)
        .title("Clean Code")
        .author("Robert C. Martin")
        .totalPages(464)
        .pagesRead(0)
        .createdAt(sevenDaysBeforeToday)
        .updatedAt(sevenDaysBeforeToday)
        .build();

    BookModelInput input = new BookModelInput("Clean Code Pdf", "Robert C. Martin", 500);

    when(bookGateway.findByIdAndUserId(bookSaved.getId(), validUser.getId())).thenReturn(bookSaved);

    OffsetDateTime now = OffsetDateTime.now();

    when(bookGateway.update(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(b.getId())
          .user(b.getUser())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .createdAt(b.getCreatedAt())
          .updatedAt(now)
          .build();
    });

    BookModelOutPut response = updateBookUseCase.execute(bookSaved.getId(), input, validUser.getId());

    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals("Clean Code Pdf", response.title());
    assertEquals(bookSaved.getCreatedAt(), response.createdAt());
    assertEquals(now, response.updatedAt());
    verify(bookGateway).findByIdAndUserId(bookSaved.getId(), validUser.getId());
    verify(bookGateway).update(any(Book.class));
  }
}
