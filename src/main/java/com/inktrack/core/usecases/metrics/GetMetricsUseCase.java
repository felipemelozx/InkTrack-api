package com.inktrack.core.usecases.metrics;

import java.util.UUID;

public interface GetMetricsUseCase {
  MetricsOutput execute(UUID userId);
}