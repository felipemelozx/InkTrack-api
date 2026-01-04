package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBooksUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  @InjectMocks
  private GetBooksUseCaseImpl getBooksUseCase;

  private UUID userId;
  private GetBookFilter filter;
  private Category validCategory;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    filter = new GetBookFilter(0, 10, "", OrderEnum.RECENT);
    validCategory = new Category(1L, "FICTION", OffsetDateTime.now());
  }

  @Test
  @DisplayName("Should return paginated books when user has books")
  void execute_shouldReturnBooks_whenBooksExist() {
    User user = new User(
        userId,
        "Test User",
        "test@email.com",
        "StrongPassword!23",
        LocalDateTime.now()
    );

    Book book = Book.builder()
        .id(1L)
        .user(user)
        .category(validCategory)
        .title("Clean Code")
        .author("Robert C. Martin")
        .totalPages(464)
        .pagesRead(100)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

    when(bookGateway.getUserBooksPage(userId, filter))
        .thenReturn(List.of(book));

    when(bookGateway.countUserBooks(userId))
        .thenReturn(1L);

    PageResult<BookModelOutput> result =
        getBooksUseCase.execute(userId, filter);

    assertNotNull(result);
    assertEquals(10, result.pageSize());
    assertEquals(1, result.totalPages());
    assertEquals(0, result.currentPage());
    assertEquals(1, result.data().size());

    BookModelOutput output = result.data().get(0);
    assertEquals("Clean Code", output.title());
    assertEquals("Robert C. Martin", output.author());
    assertEquals(464, output.totalPages());

    verify(bookGateway).getUserBooksPage(userId, filter);
    verify(bookGateway).countUserBooks(userId);
  }

  @Test
  @DisplayName("Should return empty page when user has no books")
  void execute_shouldReturnEmptyPage_whenNoBooksFound() {
    when(bookGateway.getUserBooksPage(userId, filter))
        .thenReturn(List.of());

    when(bookGateway.countUserBooks(userId))
        .thenReturn(0L);

    PageResult<BookModelOutput> result =
        getBooksUseCase.execute(userId, filter);

    assertNotNull(result);
    assertEquals(0, result.data().size());
    assertEquals(0, result.totalPages());
    assertEquals(10, result.pageSize());
    assertEquals(0, result.currentPage());

    verify(bookGateway).getUserBooksPage(userId, filter);
    verify(bookGateway).countUserBooks(userId);
  }


  @Test
  @DisplayName("Should calculate total pages correctly")
  void execute_shouldCalculateTotalPagesCorrectly() {
    GetBookFilter customFilter =
        new GetBookFilter(0, 5, "", OrderEnum.RECENT);

    User user = new User(
        userId,
        "Test User",
        "test@email.com",
        "SomePassword!322",
        LocalDateTime.now()
    );

    Book book = Book.builder()
        .id(1L)
        .user(user)
        .category(validCategory)
        .title("Any Book")
        .author("Any Author")
        .totalPages(100)
        .pagesRead(0)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

    when(bookGateway.getUserBooksPage(userId, customFilter))
        .thenReturn(List.of(book));

    when(bookGateway.countUserBooks(userId))
        .thenReturn(12L);

    PageResult<BookModelOutput> result =
        getBooksUseCase.execute(userId, customFilter);

    assertEquals(3, result.totalPages());
  }

  @Test
  @DisplayName("Should map user data into output correctly")
  void execute_shouldMapUserCorrectly() {
    User user = new User(
        userId,
        "John Doe",
        "john@email.com",
        "StrongPassword1!",
        LocalDateTime.now()
    );

    Book book = Book.builder()
        .id(10L)
        .user(user)
        .category(validCategory)
        .title("DDD")
        .author("Eric Evans")
        .totalPages(500)
        .pagesRead(50)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

    when(bookGateway.getUserBooksPage(userId, filter))
        .thenReturn(List.of(book));

    when(bookGateway.countUserBooks(userId))
        .thenReturn(1L);

    PageResult<BookModelOutput> result =
        getBooksUseCase.execute(userId, filter);

    BookModelOutput output = result.data().get(0);

    assertEquals(userId, output.user().id());
    assertEquals("John Doe", output.user().name());
    assertEquals("john@email.com", output.user().email());
  }

}
