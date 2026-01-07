package com.inktrack.infrastructure.controller;

import com.inktrack.core.usecases.metrics.BooksByCategoryOutput;
import com.inktrack.core.usecases.metrics.EvolutionPeriod;
import com.inktrack.core.usecases.metrics.GetBooksByCategoryUseCase;
import com.inktrack.core.usecases.metrics.GetMetricsUseCase;
import com.inktrack.core.usecases.metrics.GetReadingEvolutionUseCase;
import com.inktrack.core.usecases.metrics.GetReadingSessionMetricsUseCase;
import com.inktrack.core.usecases.metrics.MetricsOutput;
import com.inktrack.core.usecases.metrics.ReadingEvolutionOutput;
import com.inktrack.core.usecases.metrics.ReadingSessionMetricsOutput;
import com.inktrack.infrastructure.dtos.metrics.BooksByCategoryResponse;
import com.inktrack.infrastructure.dtos.metrics.MetricsResponse;
import com.inktrack.infrastructure.dtos.metrics.ReadingEvolutionResponse;
import com.inktrack.infrastructure.dtos.metrics.ReadingSessionMetricsResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.MetricsMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

  private final GetMetricsUseCase getMetricsUseCase;
  private final GetReadingSessionMetricsUseCase getReadingSessionMetricsUseCase;
  private final GetBooksByCategoryUseCase getBooksByCategoryUseCase;
  private final GetReadingEvolutionUseCase getReadingEvolutionUseCase;
  private final MetricsMapper metricsMapper;

  public MetricsController(
      GetMetricsUseCase getMetricsUseCase,
      GetReadingSessionMetricsUseCase getReadingSessionMetricsUseCase,
      GetBooksByCategoryUseCase getBooksByCategoryUseCase,
      GetReadingEvolutionUseCase getReadingEvolutionUseCase,
      MetricsMapper metricsMapper
  ) {
    this.getMetricsUseCase = getMetricsUseCase;
    this.getReadingSessionMetricsUseCase = getReadingSessionMetricsUseCase;
    this.getBooksByCategoryUseCase = getBooksByCategoryUseCase;
    this.getReadingEvolutionUseCase = getReadingEvolutionUseCase;
    this.metricsMapper = metricsMapper;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<MetricsResponse>> getMetrics(
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    MetricsOutput metricsOutput = getMetricsUseCase.execute(currentUser.getId());
    MetricsResponse response = metricsMapper.metricsOutputToResponse(metricsOutput);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/reading-sessions")
  public ResponseEntity<ApiResponse<ReadingSessionMetricsResponse>> getReadingSessionMetrics(
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    ReadingSessionMetricsOutput metricsOutput = getReadingSessionMetricsUseCase.execute(currentUser.getId());
    ReadingSessionMetricsResponse response = metricsMapper.readingSessionMetricsOutputToResponse(metricsOutput);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/categories")
  public ResponseEntity<ApiResponse<BooksByCategoryResponse>> getBooksByCategory(
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    BooksByCategoryOutput output = getBooksByCategoryUseCase.execute(currentUser.getId());
    BooksByCategoryResponse response = metricsMapper.booksByCategoryOutputToResponse(output);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/evolution")
  public ResponseEntity<ApiResponse<ReadingEvolutionResponse>> getReadingEvolution(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(defaultValue = "30d") String period
  ) {
    EvolutionPeriod evolutionPeriod = EvolutionPeriod.fromCode(period);
    ReadingEvolutionOutput output = getReadingEvolutionUseCase.execute(currentUser.getId(), evolutionPeriod);
    ReadingEvolutionResponse response = metricsMapper.readingEvolutionOutputToResponse(output);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}