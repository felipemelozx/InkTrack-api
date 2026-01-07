package com.inktrack.infrastructure.dtos.metrics;

public record ReadingSessionMetricsResponse(
    int totalSessions,
    long totalMinutes,
    double averagePagesPerMinute,
    double averagePagesPerSession
) {
}