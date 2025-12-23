package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.usecases.user.UserOutput;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UpdateBookUseCaseImpl implements UpdateBookUseCase{

  private final BookGateway bookGateway;

  public UpdateBookUseCaseImpl(BookGateway bookGateway) {
    this.bookGateway = bookGateway;
  }

  @Override
  public BookModelOutput execute(Long id, BookModelInput modelInput, UUID userId) {
    Book book = bookGateway.findByIdAndUserId(id, userId);

    Book bookUpdated = Book.builder()
        .id(book.getId())
        .user(book.getUser())
        .title(modelInput.title())
        .author(modelInput.author())
        .totalPages(modelInput.totalPages())
        .pagesRead(book.getPagesRead())
        .createdAt(book.getCreatedAt())
        .updatedAt(OffsetDateTime.now())
        .build();

    Book savedBook = bookGateway.update(bookUpdated);
    UserOutput userOutput = new UserOutput(
        savedBook.getUser().getId(),
        savedBook.getUser().getName(),
        savedBook.getUser().getEmail(),
        savedBook.getUser().getCreatedAt()
    );
    return new BookModelOutput(
        savedBook.getId(),
        userOutput,
        savedBook.getTitle(),
        savedBook.getAuthor(),
        savedBook.getTotalPages(),
        savedBook.getPagesRead(),
        savedBook.getProgress(),
        savedBook.getCreatedAt(),
        savedBook.getUpdatedAt()
    );
  }
}
