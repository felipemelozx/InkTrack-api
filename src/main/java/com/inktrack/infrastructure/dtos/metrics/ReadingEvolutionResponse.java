package com.inktrack.infrastructure.dtos.metrics;

import java.time.LocalDate;
import java.util.List;

public record ReadingEvolutionResponse(
    String period,
    List<ReadingEvolutionData> data
) {
  public record ReadingEvolutionData(
      LocalDate date,
      int pagesRead
  ) {
  }
}
