package com.inktrack.core.usecases.metrics;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.infrastructure.gateway.GetMetricsUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMetricsUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  private GetMetricsUseCase getMetricsUseCase;

  private UUID userId;

  @BeforeEach
  void setUp() {
    getMetricsUseCase = new GetMetricsUseCaseImpl(bookGateway);
    userId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should return metrics with calculated values when user has books")
  void execute_shouldReturnMetrics_whenUserHasBooks() {
    // Arrange
    int totalBooks = 5;
    double averageProgress = 45.5;
    int totalPagesRemaining = 150;
    int expectedDaysToFinish = 5; // 150 / 30 = 5

    when(bookGateway.getTotalBooksByUserId(userId)).thenReturn(totalBooks);
    when(bookGateway.getAverageProgressByUserId(userId)).thenReturn(averageProgress);
    when(bookGateway.getTotalPagesRemainingByUserId(userId)).thenReturn(totalPagesRemaining);

    // Act
    MetricsOutput output = getMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalBooks, output.totalBooks());
    assertEquals(averageProgress, output.averageProgress());
    assertEquals(totalPagesRemaining, output.totalPagesRemaining());
    assertEquals(expectedDaysToFinish, output.estimatedDaysToFinish());

    verify(bookGateway).getTotalBooksByUserId(userId);
    verify(bookGateway).getAverageProgressByUserId(userId);
    verify(bookGateway).getTotalPagesRemainingByUserId(userId);
  }

  @Test
  @DisplayName("Should return zero estimated days when no pages remaining")
  void execute_shouldReturnZeroDays_whenNoPagesRemaining() {
    // Arrange
    int totalBooks = 3;
    double averageProgress = 100.0;
    int totalPagesRemaining = 0;
    int expectedDaysToFinish = 0;

    when(bookGateway.getTotalBooksByUserId(userId)).thenReturn(totalBooks);
    when(bookGateway.getAverageProgressByUserId(userId)).thenReturn(averageProgress);
    when(bookGateway.getTotalPagesRemainingByUserId(userId)).thenReturn(totalPagesRemaining);

    // Act
    MetricsOutput output = getMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalBooks, output.totalBooks());
    assertEquals(averageProgress, output.averageProgress());
    assertEquals(totalPagesRemaining, output.totalPagesRemaining());
    assertEquals(expectedDaysToFinish, output.estimatedDaysToFinish());
  }

  @Test
  @DisplayName("Should calculate estimated days correctly with rounding up")
  void execute_shouldRoundUpDays_whenCalculatingEstimatedDays() {
    // Arrange
    int totalBooks = 2;
    double averageProgress = 50.0;
    int totalPagesRemaining = 31; // 31 / 30 = 1.033, should round up to 2
    int expectedDaysToFinish = 2;

    when(bookGateway.getTotalBooksByUserId(userId)).thenReturn(totalBooks);
    when(bookGateway.getAverageProgressByUserId(userId)).thenReturn(averageProgress);
    when(bookGateway.getTotalPagesRemainingByUserId(userId)).thenReturn(totalPagesRemaining);

    // Act
    MetricsOutput output = getMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(expectedDaysToFinish, output.estimatedDaysToFinish());
  }

  @Test
  @DisplayName("Should return metrics with zero values when user has no books")
  void execute_shouldReturnZeroMetrics_whenUserHasNoBooks() {
    // Arrange
    int totalBooks = 0;
    double averageProgress = 0.0;
    int totalPagesRemaining = 0;
    int expectedDaysToFinish = 0;

    when(bookGateway.getTotalBooksByUserId(userId)).thenReturn(totalBooks);
    when(bookGateway.getAverageProgressByUserId(userId)).thenReturn(averageProgress);
    when(bookGateway.getTotalPagesRemainingByUserId(userId)).thenReturn(totalPagesRemaining);

    // Act
    MetricsOutput output = getMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalBooks, output.totalBooks());
    assertEquals(averageProgress, output.averageProgress());
    assertEquals(totalPagesRemaining, output.totalPagesRemaining());
    assertEquals(expectedDaysToFinish, output.estimatedDaysToFinish());
  }

  @Test
  @DisplayName("Should handle large page count correctly")
  void execute_shouldHandleLargePageCount_whenCalculatingEstimatedDays() {
    // Arrange
    int totalBooks = 10;
    double averageProgress = 25.0;
    int totalPagesRemaining = 1000; // 1000 / 30 = 33.33, should round up to 34
    int expectedDaysToFinish = 34;

    when(bookGateway.getTotalBooksByUserId(userId)).thenReturn(totalBooks);
    when(bookGateway.getAverageProgressByUserId(userId)).thenReturn(averageProgress);
    when(bookGateway.getTotalPagesRemainingByUserId(userId)).thenReturn(totalPagesRemaining);

    // Act
    MetricsOutput output = getMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(expectedDaysToFinish, output.estimatedDaysToFinish());
  }
}