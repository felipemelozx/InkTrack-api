package com.inktrack.core.usecases.metrics;

import java.time.LocalDate;
import java.util.List;

public record ReadingEvolutionOutput(
    String period,
    List<ReadingEvolutionData> data
) {
  public record ReadingEvolutionData(
      LocalDate date,
      int pagesRead
  ) {
  }
}
