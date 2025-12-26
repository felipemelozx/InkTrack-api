package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.usecases.user.UserOutput;

public class CreateBookUseCaseImpl implements CreateBookUseCase {

  private final BookGateway bookGateway;

  public CreateBookUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public BookModelOutput execute(BookModelInput modelInput, User currentUser) {
    if (currentUser == null) {
      throw new IllegalArgumentException("User not logged in");
    }

    if(modelInput.totalPages() < 0) {
      throw new IllegalArgumentException("The total pages must be greater than zero.");
    }

    if(modelInput.title().isBlank() || modelInput.author().isBlank()) {
      throw new IllegalArgumentException("The title and author not can be black or null.");
    }
    Book book = Book.builder()
        .user(currentUser)
        .title(modelInput.title())
        .author(modelInput.author())
        .totalPages(modelInput.totalPages())
        .build();

    Book bookSaved = bookGateway.save(book);
    UserOutput userOutput = new UserOutput(
        bookSaved.getUser().getId(),
        bookSaved.getUser().getName(),
        bookSaved.getUser().getEmail(),
        bookSaved.getUser().getCreatedAt()
    );
    return new BookModelOutput(
        bookSaved.getId(),
        userOutput,
        bookSaved.getTitle(),
        bookSaved.getAuthor(),
        bookSaved.getTotalPages(),
        bookSaved.getPagesRead(),
        bookSaved.getProgress(),
        bookSaved.getCreatedAt(),
        bookSaved.getUpdatedAt()
    );
  }
}
