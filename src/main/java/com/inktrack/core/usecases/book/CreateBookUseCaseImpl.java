package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;

public class CreateBookUseCaseImpl implements CreateBookUseCase {

  private final BookGateway bookGateway;

  public CreateBookUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public Book execute(BookModelInput modelInput, User currentUser) {
    if (currentUser == null) throw new IllegalArgumentException("User not logged in");

    Book book = new Book(
        currentUser,
        modelInput.title(),
        modelInput.author(),
        modelInput.totalPages(),
        modelInput.pagesRead()
    );
    return bookGateway.save(book);
  }
}
