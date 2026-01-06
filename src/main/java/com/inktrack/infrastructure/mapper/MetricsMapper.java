package com.inktrack.infrastructure.mapper;

import com.inktrack.core.usecases.metrics.BooksByCategoryOutput;
import com.inktrack.core.usecases.metrics.MetricsOutput;
import com.inktrack.core.usecases.metrics.ReadingEvolutionOutput;
import com.inktrack.core.usecases.metrics.ReadingSessionMetricsOutput;
import com.inktrack.infrastructure.dtos.metrics.BooksByCategoryResponse;
import com.inktrack.infrastructure.dtos.metrics.MetricsResponse;
import com.inktrack.infrastructure.dtos.metrics.ReadingEvolutionResponse;
import com.inktrack.infrastructure.dtos.metrics.ReadingSessionMetricsResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetricsMapper {

  public MetricsResponse metricsOutputToResponse(MetricsOutput metricsOutput) {
    return new MetricsResponse(
        metricsOutput.totalBooks(),
        metricsOutput.averageProgress(),
        metricsOutput.totalPagesRemaining(),
        metricsOutput.estimatedDaysToFinish()
    );
  }

  public ReadingSessionMetricsResponse readingSessionMetricsOutputToResponse(
      ReadingSessionMetricsOutput output) {
    return new ReadingSessionMetricsResponse(
        output.totalSessions(),
        output.totalMinutes(),
        output.averagePagesPerMinute(),
        output.averagePagesPerSession()
    );
  }

  public BooksByCategoryResponse booksByCategoryOutputToResponse(
      BooksByCategoryOutput output) {
    List<BooksByCategoryResponse.CategoryBookCount> categories = output.categories().stream()
        .map(categoryCount -> new BooksByCategoryResponse.CategoryBookCount(
            categoryCount.categoryName(),
            categoryCount.bookCount()
        ))
        .toList();

    return new BooksByCategoryResponse(categories);
  }

  public ReadingEvolutionResponse readingEvolutionOutputToResponse(
      ReadingEvolutionOutput output) {
    List<ReadingEvolutionResponse.ReadingEvolutionData> data = output.data().stream()
        .map(evolutionData -> new ReadingEvolutionResponse.ReadingEvolutionData(
            evolutionData.date(),
            evolutionData.pagesRead()
        ))
        .toList();

    return new ReadingEvolutionResponse(output.period(), data);
  }
}