package com.inktrack.infrastructure.gateway;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.usecases.metrics.GetMetricsUseCase;
import com.inktrack.core.usecases.metrics.MetricsOutput;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetMetricsUseCaseImpl implements GetMetricsUseCase {

  private static final int PAGES_PER_DAY = 30;

  private final BookGateway bookGateway;

  public GetMetricsUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public MetricsOutput execute(UUID userId) {
    int totalBooks = bookGateway.getTotalBooksByUserId(userId);
    double averageProgress = bookGateway.getAverageProgressByUserId(userId);
    int totalPagesRemaining = bookGateway.getTotalPagesRemainingByUserId(userId);
    int estimatedDaysToFinish = calculateEstimatedDays(totalPagesRemaining);

    return new MetricsOutput(
        totalBooks,
        averageProgress,
        totalPagesRemaining,
        estimatedDaysToFinish
    );
  }

  private int calculateEstimatedDays(int totalPagesRemaining) {
    if (totalPagesRemaining == 0) {
      return 0;
    }
    return (int) Math.ceil((double) totalPagesRemaining / PAGES_PER_DAY);
  }
}