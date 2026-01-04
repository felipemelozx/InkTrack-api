package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;

public class CreateBookUseCaseImpl implements CreateBookUseCase {

  private final BookGateway bookGateway;
  private final CategoryGateway categoryGateway;

  public CreateBookUseCaseImpl(BookGateway bookGateway, CategoryGateway categoryGateway) {
    this.bookGateway = bookGateway;
    this.categoryGateway = categoryGateway;
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

    Category category = categoryGateway.getById(modelInput.categoryId())
        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + modelInput.categoryId()));

    Book book = Book.builder()
        .user(currentUser)
        .category(category)
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
    CategoryOutput categoryOutput = new CategoryOutput(
        bookSaved.getCategory().id(),
        bookSaved.getCategory().name(),
        bookSaved.getCategory().createdAt()
    );
    return new BookModelOutput(
        bookSaved.getId(),
        userOutput,
        categoryOutput,
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
