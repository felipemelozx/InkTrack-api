package com.inktrack.core.usecases.metrics;

import java.util.UUID;

public interface GetBooksByCategoryUseCase {
  BooksByCategoryOutput execute(UUID userId);
}