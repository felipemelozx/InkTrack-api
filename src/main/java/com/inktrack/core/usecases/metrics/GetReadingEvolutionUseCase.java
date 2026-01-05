package com.inktrack.core.usecases.metrics;

import java.util.UUID;

public interface GetReadingEvolutionUseCase {
  ReadingEvolutionOutput execute(UUID userId, EvolutionPeriod period);
}
