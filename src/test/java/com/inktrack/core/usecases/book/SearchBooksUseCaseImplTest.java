package com.inktrack.core.usecases.book;

import com.inktrack.core.gateway.GoogleBooksGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SearchBooksUseCaseImplTest {

  @Mock
  private GoogleBooksGateway googleBooksGateway;

  @InjectMocks
  private SearchBooksUseCaseImpl searchBooksUseCase;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void execute_withValidQuery_callsGateway() {
    String query = "Harry Potter";
    SearchBooksOutput expectedOutput = new SearchBooksOutput(10, List.of());

    when(googleBooksGateway.searchBooks(query)).thenReturn(expectedOutput);

    SearchBooksOutput result = searchBooksUseCase.execute(query);

    assertEquals(expectedOutput, result);
    verify(googleBooksGateway, times(1)).searchBooks(query);
  }

  @Test
  void execute_withNullQuery_throwsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> searchBooksUseCase.execute(null)
    );

    assertEquals("Query cannot be null or empty", exception.getMessage());
  }

  @Test
  void execute_withEmptyQuery_throwsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> searchBooksUseCase.execute("")
    );

    assertEquals("Query cannot be null or empty", exception.getMessage());
  }

  @Test
  void execute_withBlankQuery_throwsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> searchBooksUseCase.execute("   ")
    );

    assertEquals("Query cannot be null or empty", exception.getMessage());
  }

  @Test
  void execute_withTooLongQuery_throwsIllegalArgumentException() {
    String tooLongQuery = "a".repeat(501);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> searchBooksUseCase.execute(tooLongQuery)
    );

    assertEquals("Query too long (max 500 characters)", exception.getMessage());
  }

  @Test
  void execute_withQueryContainingWhitespace_trimsQuery() {
    String query = "  Harry Potter  ";
    SearchBooksOutput expectedOutput = new SearchBooksOutput(10, List.of());

    when(googleBooksGateway.searchBooks("Harry Potter")).thenReturn(expectedOutput);

    searchBooksUseCase.execute(query);

    verify(googleBooksGateway, times(1)).searchBooks("Harry Potter");
  }
}
