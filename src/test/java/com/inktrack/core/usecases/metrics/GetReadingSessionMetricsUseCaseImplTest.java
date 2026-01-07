package com.inktrack.core.usecases.metrics;

import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.infrastructure.gateway.GetReadingSessionMetricsUseCaseImpl;
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
class GetReadingSessionMetricsUseCaseImplTest {

  @Mock
  private ReadingSessionGateway readingSessionGateway;

  private GetReadingSessionMetricsUseCase getReadingSessionMetricsUseCase;

  private UUID userId;

  @BeforeEach
  void setUp() {
    getReadingSessionMetricsUseCase = new GetReadingSessionMetricsUseCaseImpl(readingSessionGateway);
    userId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should return session metrics when user has reading sessions")
  void execute_shouldReturnSessionMetrics_whenUserHasReadingSessions() {
    // Arrange
    int totalSessions = 20;
    long totalMinutes = 300L;
    double averagePagesPerMinute = 2.5;
    double averagePagesPerSession = 15.0;

    when(readingSessionGateway.getTotalSessionsByUserId(userId)).thenReturn(totalSessions);
    when(readingSessionGateway.getTotalMinutesByUserId(userId)).thenReturn(totalMinutes);
    when(readingSessionGateway.getAveragePagesPerMinuteByUserId(userId)).thenReturn(averagePagesPerMinute);
    when(readingSessionGateway.getAveragePagesPerSessionByUserId(userId)).thenReturn(averagePagesPerSession);

    // Act
    ReadingSessionMetricsOutput output = getReadingSessionMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalSessions, output.totalSessions());
    assertEquals(totalMinutes, output.totalMinutes());
    assertEquals(averagePagesPerMinute, output.averagePagesPerMinute());
    assertEquals(averagePagesPerSession, output.averagePagesPerSession());

    verify(readingSessionGateway).getTotalSessionsByUserId(userId);
    verify(readingSessionGateway).getTotalMinutesByUserId(userId);
    verify(readingSessionGateway).getAveragePagesPerMinuteByUserId(userId);
    verify(readingSessionGateway).getAveragePagesPerSessionByUserId(userId);
  }

  @Test
  @DisplayName("Should return zero metrics when user has no reading sessions")
  void execute_shouldReturnZeroMetrics_whenUserHasNoReadingSessions() {
    // Arrange
    int totalSessions = 0;
    long totalMinutes = 0L;
    double averagePagesPerMinute = 0.0;
    double averagePagesPerSession = 0.0;

    when(readingSessionGateway.getTotalSessionsByUserId(userId)).thenReturn(totalSessions);
    when(readingSessionGateway.getTotalMinutesByUserId(userId)).thenReturn(totalMinutes);
    when(readingSessionGateway.getAveragePagesPerMinuteByUserId(userId)).thenReturn(averagePagesPerMinute);
    when(readingSessionGateway.getAveragePagesPerSessionByUserId(userId)).thenReturn(averagePagesPerSession);

    // Act
    ReadingSessionMetricsOutput output = getReadingSessionMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalSessions, output.totalSessions());
    assertEquals(totalMinutes, output.totalMinutes());
    assertEquals(averagePagesPerMinute, output.averagePagesPerMinute());
    assertEquals(averagePagesPerSession, output.averagePagesPerSession());
  }

  @Test
  @DisplayName("Should handle decimal values correctly in averages")
  void execute_shouldHandleDecimals_whenCalculatingAverages() {
    // Arrange
    int totalSessions = 15;
    long totalMinutes = 245L;
    double averagePagesPerMinute = 1.75;
    double averagePagesPerSession = 28.33;

    when(readingSessionGateway.getTotalSessionsByUserId(userId)).thenReturn(totalSessions);
    when(readingSessionGateway.getTotalMinutesByUserId(userId)).thenReturn(totalMinutes);
    when(readingSessionGateway.getAveragePagesPerMinuteByUserId(userId)).thenReturn(averagePagesPerMinute);
    when(readingSessionGateway.getAveragePagesPerSessionByUserId(userId)).thenReturn(averagePagesPerSession);

    // Act
    ReadingSessionMetricsOutput output = getReadingSessionMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalSessions, output.totalSessions());
    assertEquals(totalMinutes, output.totalMinutes());
    assertEquals(averagePagesPerMinute, output.averagePagesPerMinute());
    assertEquals(averagePagesPerSession, output.averagePagesPerSession());
  }

  @Test
  @DisplayName("Should handle large minute counts correctly")
  void execute_shouldHandleLargeMinuteCount_whenCalculatingMetrics() {
    // Arrange
    int totalSessions = 100;
    long totalMinutes = 5000L;
    double averagePagesPerMinute = 3.0;
    double averagePagesPerSession = 150.0;

    when(readingSessionGateway.getTotalSessionsByUserId(userId)).thenReturn(totalSessions);
    when(readingSessionGateway.getTotalMinutesByUserId(userId)).thenReturn(totalMinutes);
    when(readingSessionGateway.getAveragePagesPerMinuteByUserId(userId)).thenReturn(averagePagesPerMinute);
    when(readingSessionGateway.getAveragePagesPerSessionByUserId(userId)).thenReturn(averagePagesPerSession);

    // Act
    ReadingSessionMetricsOutput output = getReadingSessionMetricsUseCase.execute(userId);

    // Assert
    assertNotNull(output);
    assertEquals(totalSessions, output.totalSessions());
    assertEquals(totalMinutes, output.totalMinutes());
    assertEquals(averagePagesPerMinute, output.averagePagesPerMinute());
    assertEquals(averagePagesPerSession, output.averagePagesPerSession());
  }
}