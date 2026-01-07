package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UpdateBookUseCaseImpl implements UpdateBookUseCase{

  private final BookGateway bookGateway;
  private final CategoryGateway categoryGateway;

  public UpdateBookUseCaseImpl(BookGateway bookGateway, CategoryGateway categoryGateway) {
    this.bookGateway = bookGateway;
    this.categoryGateway = categoryGateway;
  }

  @Override
  public BookModelOutput execute(Long id, BookModelInput modelInput, UUID userId) {
    Book book = bookGateway.findByIdAndUserId(id, userId);

    var category = categoryGateway.getById(modelInput.categoryId())
        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + modelInput.categoryId()));

    Book bookUpdated = Book.builder()
        .id(book.getId())
        .user(book.getUser())
        .category(category)
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
    CategoryOutput categoryOutput = new CategoryOutput(
        savedBook.getCategory().id(),
        savedBook.getCategory().name(),
        savedBook.getCategory().createdAt()
    );
    return new BookModelOutput(
        savedBook.getId(),
        userOutput,
        categoryOutput,
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
