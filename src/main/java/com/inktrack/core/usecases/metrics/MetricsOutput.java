package com.inktrack.core.usecases.metrics;

public record MetricsOutput(
    int totalBooks,
    double averageProgress,
    int totalPagesRemaining,
    int estimatedDaysToFinish
) {
}