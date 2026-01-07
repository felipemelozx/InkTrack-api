package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBookUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  @Mock
  private CategoryGateway categoryGateway;

  private CreateBookUseCaseImpl createBookUseCase;

  private User validUser;

  private Category validCategory;

  @BeforeEach
  void setUp() {
    createBookUseCase = new CreateBookUseCaseImpl(bookGateway, categoryGateway);
    validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
    validCategory = new Category(1L, "FICTION", OffsetDateTime.now());
  }

  @Test
  @DisplayName("Should create book successfully when all data is valid")
  void execute_shouldCreateBook_whenDataIsValid() {
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L);
    OffsetDateTime now = OffsetDateTime.now();

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));

    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1l)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals("Clean Code", response.title());
    assertEquals(now, response.createdAt());
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should throw exception when user is null")
  void execute_shouldThrowException_whenUserIsNull() {
    BookModelInput input = new BookModelInput("Title", "Author", 100, 1L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        createBookUseCase.execute(input, null)
    );

    assertEquals("User not logged in", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should throw exception when title is blank")
  void execute_shouldThrowException_whenTitleIsBlank() {
    BookModelInput input = new BookModelInput("", "Author", 100, 1L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        createBookUseCase.execute(input, validUser)
    );

    assertEquals("The title and author not can be black or null.", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should throw exception when author is blank")
  void execute_shouldThrowException_whenAuthorIsBlank() {
    BookModelInput input = new BookModelInput("Title", "   ", 100, 1L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        createBookUseCase.execute(input, validUser)
    );

    assertEquals("The title and author not can be black or null.", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should throw exception when total pages is negative")
  void execute_shouldThrowException_whenTotalPagesIsNegative() {
    BookModelInput input =
        new BookModelInput("Clean Code", "Robert C. Martin", -1, 1L);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> createBookUseCase.execute(input, validUser)
    );

    assertEquals(
        "The total pages must be greater than zero.",
        exception.getMessage()
    );

    verifyNoInteractions(bookGateway);
  }
}
