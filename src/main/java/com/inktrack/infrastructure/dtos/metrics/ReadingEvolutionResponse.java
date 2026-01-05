package com.inktrack.infrastructure.dtos.metrics;

import java.time.LocalDate;

public record ReadingEvolutionResponse(
    String period,
    ReadingEvolutionData[] data
) {
  public record ReadingEvolutionData(
      LocalDate date,
      int pagesRead
  ) {
  }
}
