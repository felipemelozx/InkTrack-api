package com.inktrack.core.usecases.book;

import com.inktrack.core.exception.BookNotFoundException;
import com.inktrack.core.gateway.BookGateway;

import java.util.UUID;

public class DeleteBookUseCaseImpl implements DeleteBookUseCase {

  private final BookGateway bookGateway;

  public DeleteBookUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public void execute(Long bookId, UUID userId) {
    boolean deleted = bookGateway.deleteByIdAndUserId(bookId, userId);
    if (!deleted) {
      throw new BookNotFoundException("id", "Book not found with id: " + bookId);
    }
  }
}
