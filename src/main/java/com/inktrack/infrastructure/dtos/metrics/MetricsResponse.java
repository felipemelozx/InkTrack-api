package com.inktrack.infrastructure.dtos.metrics;

public record MetricsResponse(
    int totalBooks,
    double averageProgress,
    int totalPagesRemaining,
    int estimatedDaysToFinish
) {
}