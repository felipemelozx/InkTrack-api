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
  public BookModelOutPut execute(BookModelInput modelInput, User currentUser) {
    if (currentUser == null) throw new IllegalArgumentException("User not logged in");

    if(modelInput.totalPages() < 0) throw new IllegalArgumentException("The total pages must be greater than zero.");
    if(modelInput.title().isBlank() || modelInput.author().isBlank()) throw new IllegalArgumentException("The title and author not can be black or null.");
    Book book = new Book(
        currentUser,
        modelInput.title(),
        modelInput.author(),
        modelInput.totalPages()
    );
    Book bookSaved = bookGateway.save(book);
    return new BookModelOutPut(
        bookSaved.getId(),
        bookSaved.getUser(),
        bookSaved.getTitle(),
        bookSaved.getAuthor(),
        bookSaved.getTotalPages(),
        bookSaved.getPagesRead(),
        bookSaved.getCreatedAt(),
        bookSaved.getUpdatedAt()
    );
  }
}
