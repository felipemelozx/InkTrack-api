package com.inktrack.infrastructure.dtos.metrics;

import java.util.List;

public record BooksByCategoryResponse(
    List<CategoryBookCount> categories
) {
  public record CategoryBookCount(
      String categoryName,
      long bookCount
  ) {
  }
}