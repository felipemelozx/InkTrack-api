package com.inktrack.infrastructure.gateway;

import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.usecases.metrics.GetReadingSessionMetricsUseCase;
import com.inktrack.core.usecases.metrics.ReadingSessionMetricsOutput;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetReadingSessionMetricsUseCaseImpl implements GetReadingSessionMetricsUseCase {

  private final ReadingSessionGateway readingSessionGateway;

  public GetReadingSessionMetricsUseCaseImpl(ReadingSessionGateway readingSessionGateway) {
    this.readingSessionGateway = readingSessionGateway;
  }

  @Override
  public ReadingSessionMetricsOutput execute(UUID userId) {
    int totalSessions = readingSessionGateway.getTotalSessionsByUserId(userId);
    long totalMinutes = readingSessionGateway.getTotalMinutesByUserId(userId);
    double averagePagesPerMinute = readingSessionGateway.getAveragePagesPerMinuteByUserId(userId);
    double averagePagesPerSession = readingSessionGateway.getAveragePagesPerSessionByUserId(userId);

    return new ReadingSessionMetricsOutput(
        totalSessions,
        totalMinutes,
        averagePagesPerMinute,
        averagePagesPerSession
    );
  }
}