package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBookByIdUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  private GetBookByIdUseCase getBookByIdUseCase;

  private User validUser;

  private Category validCategory;

  @BeforeEach
  void setUp() {
    getBookByIdUseCase = new GetBookByIdUseCaseImpl(bookGateway);
    validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
    validCategory = new Category(1L, "FICTION", OffsetDateTime.now());
  }

  @Test
  @DisplayName("Should return book when book exists and belongs to user")
  void execute_shouldReturnBook_whenBookExistsAndBelongsToUser() {
    Long bookId = 1L;
    OffsetDateTime now = OffsetDateTime.now();

    Book book = Book.builder()
        .id(bookId)
        .user(validUser)
        .category(validCategory)
        .title("Clean Code")
        .author("Robert C. Martin")
        .totalPages(300)
        .pagesRead(100)
        .createdAt(now)
        .updatedAt(now)
        .build();

    when(bookGateway.findByIdAndUserId(bookId, validUser.getId()))
        .thenReturn(book);

    BookModelOutput output =
        getBookByIdUseCase.execute(bookId, validUser.getId());

    assertNotNull(output);
    assertEquals(bookId, output.id());
    assertEquals("Clean Code", output.title());
    assertEquals("Robert C. Martin", output.author());
    assertEquals(300, output.totalPages());
    assertEquals(100, output.pagesRead());
    assertEquals(33, output.progress());
    assertEquals(now, output.createdAt());
    assertEquals(now, output.updatedAt());

    assertEquals(validUser.getId(), output.user().id());
    assertEquals(validUser.getName(), output.user().name());
    assertEquals(validUser.getEmail(), output.user().email());

    assertEquals(validCategory.id(), output.category().id());
    assertEquals(validCategory.name(), output.category().name());
    assertEquals(validCategory.createdAt(), output.category().createdAt());

    verify(bookGateway)
        .findByIdAndUserId(bookId, validUser.getId());
  }

  @Test
  @DisplayName("Should throw exception when book id is null")
  void execute_shouldThrowException_whenBookIdIsNull() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, this::executeGetBookWithNullBookId);

    assertEquals("Book ID or User ID is null", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  private BookModelOutput executeGetBookWithNullBookId() {
    return getBookByIdUseCase.execute(null, validUser.getId());
  }

  @Test
  @DisplayName("Should throw exception when user id is null")
  void execute_shouldThrowException_whenUserIdIsNull() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, this::executeGetBookWithNullUserId);

    assertEquals("Book ID or User ID is null", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  private BookModelOutput executeGetBookWithNullUserId() {
    return getBookByIdUseCase.execute(1L, null);
  }
}
