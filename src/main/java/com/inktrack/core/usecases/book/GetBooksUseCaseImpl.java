package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;
import com.inktrack.core.utils.PageResult;

import java.util.List;
import java.util.UUID;

public class GetBooksUseCaseImpl implements GetBooksUseCase {

  private final BookGateway bookGateway;

  public GetBooksUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public PageResult<BookModelOutput> execute(UUID userId, GetBookFilter filter) {
    List<Book> books = bookGateway.getUserBooksPage(
        userId,
        filter
    );

    Long total = bookGateway.countUserBooksWithFilters(userId, filter.title(), filter.categoryId());

    Integer totalPages = (int) Math.ceil((double) total / filter.size());

    List<BookModelOutput> output =
        books.stream()
            .map(b -> {
              UserOutput userOutput = new UserOutput(
                  b.getUser().getId(),
                  b.getUser().getName(),
                  b.getUser().getEmail(),
                  b.getUser().getCreatedAt()
              );
              CategoryOutput categoryOutput = new CategoryOutput(
                  b.getCategory().id(),
                  b.getCategory().name(),
                  b.getCategory().createdAt()
              );
              return new BookModelOutput(
                  b.getId(),
                  userOutput,
                  categoryOutput,
                  b.getTitle(),
                  b.getAuthor(),
                  b.getTotalPages(),
                  b.getPagesRead(),
                  b.getProgress(),
                  b.getThumbnailUrl(),
                  b.getCreatedAt(),
                  b.getUpdatedAt()
              );
            })
            .toList();

    return new PageResult<>(
        filter.size(),
        totalPages,
        filter.page(),
        output
    );
  }
}
