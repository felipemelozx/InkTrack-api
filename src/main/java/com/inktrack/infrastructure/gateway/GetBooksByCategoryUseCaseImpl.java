package com.inktrack.infrastructure.gateway;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.BookGateway.CategoryBookCount;
import com.inktrack.core.usecases.metrics.BooksByCategoryOutput;
import com.inktrack.core.usecases.metrics.GetBooksByCategoryUseCase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetBooksByCategoryUseCaseImpl implements GetBooksByCategoryUseCase {

  private final BookGateway bookGateway;

  public GetBooksByCategoryUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public BooksByCategoryOutput execute(UUID userId) {
    List<CategoryBookCount> categoryBookCounts = bookGateway.getBooksCountByCategory(userId);

    List<BooksByCategoryOutput.CategoryBookCount> outputList = categoryBookCounts.stream()
        .map(categoryCount -> new BooksByCategoryOutput.CategoryBookCount(
            categoryCount.categoryName(),
            categoryCount.bookCount()
        ))
        .toList();

    return new BooksByCategoryOutput(outputList);
  }
}