package com.inktrack.core.usecases.metrics;

public record ReadingSessionMetricsOutput(
    int totalSessions,
    long totalMinutes,
    double averagePagesPerMinute,
    double averagePagesPerSession
) {
}