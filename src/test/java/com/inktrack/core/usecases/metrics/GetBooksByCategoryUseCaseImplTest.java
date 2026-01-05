package com.inktrack.core.usecases.metrics;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.infrastructure.gateway.GetBooksByCategoryUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBooksByCategoryUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  private GetBooksByCategoryUseCase getBooksByCategoryUseCase;

  private UUID userId;

  @BeforeEach
  void setUp() {
    getBooksByCategoryUseCase = new GetBooksByCategoryUseCaseImpl(bookGateway);
    userId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should return books grouped by category when user has books")
  void execute_shouldReturnBooksByCategory_whenUserHasBooks() {
    // Arrange
    BookGateway.CategoryBookCount fictionCount = new BookGateway.CategoryBookCount("FICTION", 5);
    BookGateway.CategoryBookCount scienceCount = new BookGateway.CategoryBookCount("SCIENCE", 3);
    BookGateway.CategoryBookCount historyCount = new BookGateway.CategoryBookCount("HISTORY", 2);

    List<BookGateway.CategoryBookCount> categoryCounts = List.of(fictionCount, scienceCount, historyCount);

    when(bookGateway.getBooksCountByCategory(userId)).thenReturn(categoryCounts);

    // Act
    BooksByCategoryOutput output = getBooksByCategoryUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(3, output.categories().size());

    assertEquals("FICTION", output.categories().get(0).categoryName());
    assertEquals(5, output.categories().get(0).bookCount());

    assertEquals("SCIENCE", output.categories().get(1).categoryName());
    assertEquals(3, output.categories().get(1).bookCount());

    assertEquals("HISTORY", output.categories().get(2).categoryName());
    assertEquals(2, output.categories().get(2).bookCount());

    verify(bookGateway).getBooksCountByCategory(userId);
  }

  @Test
  @DisplayName("Should return empty list when user has no books")
  void execute_shouldReturnEmptyList_whenUserHasNoBooks() {
    // Arrange
    List<BookGateway.CategoryBookCount> categoryCounts = List.of();

    when(bookGateway.getBooksCountByCategory(userId)).thenReturn(categoryCounts);

    // Act
    BooksByCategoryOutput output = getBooksByCategoryUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertTrue(output.categories().isEmpty());
    assertEquals(0, output.categories().size());

    verify(bookGateway).getBooksCountByCategory(userId);
  }

  @Test
  @DisplayName("Should return single category when user has books in one category")
  void execute_shouldReturnSingleCategory_whenUserHasBooksInOneCategory() {
    // Arrange
    BookGateway.CategoryBookCount fictionCount = new BookGateway.CategoryBookCount("FICTION", 10);

    List<BookGateway.CategoryBookCount> categoryCounts = List.of(fictionCount);

    when(bookGateway.getBooksCountByCategory(userId)).thenReturn(categoryCounts);

    // Act
    BooksByCategoryOutput output = getBooksByCategoryUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(1, output.categories().size());
    assertEquals("FICTION", output.categories().get(0).categoryName());
    assertEquals(10, output.categories().get(0).bookCount());

    verify(bookGateway).getBooksCountByCategory(userId);
  }

  @Test
  @DisplayName("Should preserve order from gateway")
  void execute_shouldPreserveOrder_whenGatewayReturnsOrderedData() {
    // Arrange
    BookGateway.CategoryBookCount count1 = new BookGateway.CategoryBookCount("HISTORY", 1);
    BookGateway.CategoryBookCount count2 = new BookGateway.CategoryBookCount("FICTION", 5);
    BookGateway.CategoryBookCount count3 = new BookGateway.CategoryBookCount("SCIENCE", 3);

    List<BookGateway.CategoryBookCount> categoryCounts = List.of(count1, count2, count3);

    when(bookGateway.getBooksCountByCategory(userId)).thenReturn(categoryCounts);

    // Act
    BooksByCategoryOutput output = getBooksByCategoryUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(3, output.categories().size());
    assertEquals("HISTORY", output.categories().get(0).categoryName());
    assertEquals("FICTION", output.categories().get(1).categoryName());
    assertEquals("SCIENCE", output.categories().get(2).categoryName());
  }

  @Test
  @DisplayName("Should handle categories with zero book count")
  void execute_shouldHandleZeroCount_whenCategoryHasNoBooks() {
    // Arrange
    BookGateway.CategoryBookCount fictionCount = new BookGateway.CategoryBookCount("FICTION", 5);
    BookGateway.CategoryBookCount poetryCount = new BookGateway.CategoryBookCount("POETRY", 0);
    BookGateway.CategoryBookCount scienceCount = new BookGateway.CategoryBookCount("SCIENCE", 3);

    List<BookGateway.CategoryBookCount> categoryCounts = List.of(fictionCount, poetryCount, scienceCount);

    when(bookGateway.getBooksCountByCategory(userId)).thenReturn(categoryCounts);

    // Act
    BooksByCategoryOutput output = getBooksByCategoryUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(3, output.categories().size());

    assertEquals("FICTION", output.categories().get(0).categoryName());
    assertEquals(5, output.categories().get(0).bookCount());

    assertEquals("POETRY", output.categories().get(1).categoryName());
    assertEquals(0, output.categories().get(1).bookCount());

    assertEquals("SCIENCE", output.categories().get(2).categoryName());
    assertEquals(3, output.categories().get(2).bookCount());
  }
}