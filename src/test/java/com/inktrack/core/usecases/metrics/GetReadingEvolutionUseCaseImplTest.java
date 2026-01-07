package com.inktrack.core.usecases.metrics;

import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.gateway.ReadingSessionGateway.EvolutionData;
import com.inktrack.infrastructure.gateway.GetReadingEvolutionUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReadingEvolutionUseCaseImplTest {

  @Mock
  private ReadingSessionGateway readingSessionGateway;

  private GetReadingEvolutionUseCase getReadingEvolutionUseCase;

  private UUID userId;

  @BeforeEach
  void setUp() {
    getReadingEvolutionUseCase = new GetReadingEvolutionUseCaseImpl(readingSessionGateway);
    userId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should return evolution data for 30 days period")
  void execute_shouldReturnEvolutionData_whenPeriodIs30Days() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.DAYS_30;
    LocalDate date1 = LocalDate.now().minusDays(5);
    LocalDate date2 = LocalDate.now().minusDays(3);

    List<EvolutionData> evolutionData = List.of(
        new EvolutionData(date1, 25),
        new EvolutionData(date2, 40)
    );

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(2, output.data().size());
    assertEquals(date1, output.data().get(0).date());
    assertEquals(25, output.data().get(0).pagesRead());
    assertEquals(date2, output.data().get(1).date());
    assertEquals(40, output.data().get(1).pagesRead());

    verify(readingSessionGateway).getReadingEvolution(eq(userId), any(LocalDate.class));
  }

  @Test
  @DisplayName("Should return evolution data for 3 months period")
  void execute_shouldReturnEvolutionData_whenPeriodIs3Months() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.MONTHS_3;
    LocalDate date1 = LocalDate.now().minusMonths(1);
    LocalDate date2 = LocalDate.now().minusWeeks(2);

    List<EvolutionData> evolutionData = List.of(
        new EvolutionData(date1, 100),
        new EvolutionData(date2, 150)
    );

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(2, output.data().size());
    assertEquals(date1, output.data().get(0).date());
    assertEquals(100, output.data().get(0).pagesRead());
    assertEquals(date2, output.data().get(1).date());
    assertEquals(150, output.data().get(1).pagesRead());
  }

  @Test
  @DisplayName("Should return evolution data for 6 months period")
  void execute_shouldReturnEvolutionData_whenPeriodIs6Months() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.MONTHS_6;
    LocalDate date1 = LocalDate.now().minusMonths(3);

    List<EvolutionData> evolutionData = List.of(
        new EvolutionData(date1, 200)
    );

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(1, output.data().size());
    assertEquals(date1, output.data().get(0).date());
    assertEquals(200, output.data().get(0).pagesRead());
  }

  @Test
  @DisplayName("Should return evolution data for 12 months period")
  void execute_shouldReturnEvolutionData_whenPeriodIs12Months() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.MONTHS_12;
    LocalDate date1 = LocalDate.now().minusMonths(6);
    LocalDate date2 = LocalDate.now().minusMonths(3);

    List<EvolutionData> evolutionData = List.of(
        new EvolutionData(date1, 300),
        new EvolutionData(date2, 450)
    );

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(2, output.data().size());
  }

  @Test
  @DisplayName("Should return empty data when no evolution data exists")
  void execute_shouldReturnEmptyData_whenNoEvolutionDataExists() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.DAYS_30;
    List<EvolutionData> evolutionData = List.of();

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(0, output.data().size());
  }

  @Test
  @DisplayName("Should return empty data when gateway returns null")
  void execute_shouldReturnEmptyData_whenGatewayReturnsNull() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.MONTHS_3;

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(null);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(0, output.data().size());
  }

  @Test
  @DisplayName("Should handle single evolution data point")
  void execute_shouldHandleSingleDataPoint_whenGatewayReturnsOneEntry() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.DAYS_30;
    LocalDate date1 = LocalDate.now().minusDays(1);

    List<EvolutionData> evolutionData = List.of(
        new EvolutionData(date1, 30)
    );

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(period.getCode(), output.period());
    assertEquals(1, output.data().size());
    assertEquals(date1, output.data().get(0).date());
    assertEquals(30, output.data().get(0).pagesRead());
  }

  @Test
  @DisplayName("Should preserve data order from gateway")
  void execute_shouldPreserveOrder_whenGatewayReturnsOrderedData() {
    // Arrange
    EvolutionPeriod period = EvolutionPeriod.DAYS_30;
    LocalDate date1 = LocalDate.now().minusDays(10);
    LocalDate date2 = LocalDate.now().minusDays(5);
    LocalDate date3 = LocalDate.now().minusDays(1);

    List<EvolutionData> evolutionData = List.of(
        new EvolutionData(date1, 20),
        new EvolutionData(date2, 35),
        new EvolutionData(date3, 50)
    );

    when(readingSessionGateway.getReadingEvolution(eq(userId), any(LocalDate.class))).thenReturn(evolutionData);

    // Act
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(userId, period);

    // Assert
    assertNotNull(output);
    assertEquals(3, output.data().size());
    assertEquals(date1, output.data().get(0).date());
    assertEquals(date2, output.data().get(1).date());
    assertEquals(date3, output.data().get(2).date());
  }
}