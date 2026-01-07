package com.inktrack.core.usecases.metrics;

import java.util.UUID;

public interface GetReadingSessionMetricsUseCase {
  ReadingSessionMetricsOutput execute(UUID userId);
}