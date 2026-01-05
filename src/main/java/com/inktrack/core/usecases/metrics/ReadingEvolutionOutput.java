package com.inktrack.core.usecases.metrics;

import java.time.LocalDate;

public record ReadingEvolutionOutput(
    String period,
    ReadingEvolutionData[] data
) {
  public record ReadingEvolutionData(
      LocalDate date,
      int pagesRead
  ) {
  }
}
