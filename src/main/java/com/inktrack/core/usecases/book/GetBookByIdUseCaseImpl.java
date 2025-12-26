package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.usecases.user.UserOutput;

import java.util.UUID;

public class GetBookByIdUseCaseImpl implements GetBookByIdUseCase {

  private final BookGateway bookGateway;

  public GetBookByIdUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public BookModelOutput execute(Long bookID, UUID userId) {
    if (bookID == null || userId == null) {
      throw new IllegalArgumentException("Book ID or User ID is null");
    }
    Book book = bookGateway.findByIdAndUserId(bookID, userId);
    UserOutput userOutput = new UserOutput(
        book.getUser().getId(),
        book.getUser().getName(),
        book.getUser().getEmail(),
        book.getUser().getCreatedAt()
    );
    return new BookModelOutput(
        book.getId(),
        userOutput,
        book.getTitle(),
        book.getAuthor(),
        book.getTotalPages(),
        book.getPagesRead(),
        book.getProgress(),
        book.getCreatedAt(),
        book.getUpdatedAt()
    );
  }
}
