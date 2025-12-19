package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UpdateBookUseCaseImpl implements UpdateBookUseCase{

  private final BookGateway bookGateway;

  public UpdateBookUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public BookModelOutPut execute(Long id, BookModelInput modelInput, UUID userId) {
    Book book = bookGateway.findByIdAndUserId(id, userId);

    Book bookUpdated = new Book(
        book.getId(),
        book.getUser(),
        modelInput.title(),
        modelInput.author(),
        modelInput.totalPages(),
        book.getPagesRead(),
        book.getCreatedAt(),
        OffsetDateTime.now()
    );
    Book savedBook = bookGateway.update(bookUpdated);
    return new BookModelOutPut(
        savedBook.getId(),
        savedBook.getUser(),
        savedBook.getTitle(),
        savedBook.getAuthor(),
        savedBook.getTotalPages(),
        savedBook.getPagesRead(),
        savedBook.getCreatedAt(),
        savedBook.getUpdatedAt()
    );
  }
}
