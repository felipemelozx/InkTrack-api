package com.inktrack.core.usecases.metrics;

import java.util.List;

public record BooksByCategoryOutput(
    List<CategoryBookCount> categories
) {
  public record CategoryBookCount(
      String categoryName,
      long bookCount
  ) {
  }
}