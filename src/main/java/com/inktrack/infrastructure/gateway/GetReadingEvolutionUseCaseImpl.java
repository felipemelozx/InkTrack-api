package com.inktrack.infrastructure.gateway;

import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.usecases.metrics.EvolutionPeriod;
import com.inktrack.core.usecases.metrics.GetReadingEvolutionUseCase;
import com.inktrack.core.usecases.metrics.ReadingEvolutionOutput;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class GetReadingEvolutionUseCaseImpl implements GetReadingEvolutionUseCase {

  private final ReadingSessionGateway readingSessionGateway;

  public GetReadingEvolutionUseCaseImpl(ReadingSessionGateway readingSessionGateway) {
    this.readingSessionGateway = readingSessionGateway;
  }

  @Override
  public ReadingEvolutionOutput execute(UUID userId, EvolutionPeriod period) {
    LocalDate startDate = calculateStartDate(period);

    var evolutionData = readingSessionGateway.getReadingEvolution(userId, startDate);

    if (evolutionData == null || evolutionData.isEmpty()) {
      return new ReadingEvolutionOutput(period.getCode(), new ReadingEvolutionOutput.ReadingEvolutionData[0]);
    }

    ReadingEvolutionOutput.ReadingEvolutionData[] data = evolutionData.stream()
        .map(evo -> new ReadingEvolutionOutput.ReadingEvolutionData(
            evo.date(),
            evo.totalPages()
        ))
        .toArray(ReadingEvolutionOutput.ReadingEvolutionData[]::new);

    return new ReadingEvolutionOutput(period.getCode(), data);
  }

  private LocalDate calculateStartDate(EvolutionPeriod period) {
    LocalDate now = LocalDate.now();
    return switch (period) {
      case DAYS_30 -> now.minusDays(30);
      case MONTHS_3 -> now.minusMonths(3);
      case MONTHS_6 -> now.minusMonths(6);
      case MONTHS_12 -> now.minusMonths(12);
    };
  }
}
