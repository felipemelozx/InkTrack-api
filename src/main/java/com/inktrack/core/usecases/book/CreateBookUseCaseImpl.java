package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.infrastructure.mapper.BookMapper;

public class CreateBookUseCaseImpl implements CreateBookUseCase {

  private final BookGateway bookGateway;
  private final BookMapper bookMapper;

  public CreateBookUseCaseImpl(BookGateway bookGateway, BookMapper bookMapper) {
    this.bookGateway = bookGateway;
    this.bookMapper = bookMapper;
  }

  @Override
  public Book execute(BookModelInput modelInput, User currentUser) {
    if (currentUser == null) throw new IllegalArgumentException("User not logged in");

    if(modelInput.pagesRead() < 0 || !(modelInput.totalPages() >= modelInput.pagesRead())) throw new IllegalArgumentException("The page read can not negative or above the total pages.");
    if(modelInput.title().isBlank() || modelInput.author().isBlank()) throw new IllegalArgumentException("The title and author not can be black or null.");
    Book book = bookMapper.modelInputToEntityPreSave(modelInput, currentUser);
    return bookGateway.save(book);
  }
}
