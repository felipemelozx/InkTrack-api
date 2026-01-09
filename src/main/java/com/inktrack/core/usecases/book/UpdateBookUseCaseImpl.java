package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.core.gateway.GoogleBooksGateway;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class UpdateBookUseCaseImpl implements UpdateBookUseCase{

  private final BookGateway bookGateway;
  private final CategoryGateway categoryGateway;
  private final GoogleBooksGateway googleBooksGateway;

  public UpdateBookUseCaseImpl(
      BookGateway bookGateway,
      CategoryGateway categoryGateway,
      GoogleBooksGateway googleBooksGateway
  ) {
    this.bookGateway = bookGateway;
    this.categoryGateway = categoryGateway;
    this.googleBooksGateway = googleBooksGateway;
  }

  @Override
  public BookModelOutput execute(Long id, BookModelInput modelInput, UUID userId) {
    Book book = bookGateway.findByIdAndUserId(id, userId);

    var category = categoryGateway.getById(modelInput.categoryId())
        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + modelInput.categoryId()));

    String newThumbnailUrl = fetchThumbnailIfGoogleBookIdChanged(book, modelInput.googleBookId());
    String newGoogleBookId = modelInput.googleBookId();

    Book bookUpdated = Book.builder()
        .id(book.getId())
        .user(book.getUser())
        .category(category)
        .title(modelInput.title())
        .author(modelInput.author())
        .totalPages(modelInput.totalPages())
        .pagesRead(book.getPagesRead())
        .thumbnailUrl(newThumbnailUrl)
        .googleBookId(newGoogleBookId)
        .createdAt(book.getCreatedAt())
        .updatedAt(OffsetDateTime.now())
        .build();

    Book savedBook = bookGateway.update(bookUpdated);
    return buildOutput(savedBook);
  }

  private String fetchThumbnailIfGoogleBookIdChanged(Book book, String newGoogleBookId) {
    if (newGoogleBookId != null && !newGoogleBookId.equals(book.getGoogleBookId())) {
      try {
        Optional<GoogleBooksVolume> volume = googleBooksGateway.getVolumeById(newGoogleBookId);
        return volume.map(GoogleBooksVolume::thumbnailUrl).orElse(book.getThumbnailUrl());
      } catch (Exception e) {
        System.err.println("Failed to fetch book thumbnail: " + e.getMessage());
        return book.getThumbnailUrl();
      }
    }
    return book.getThumbnailUrl();
  }

  private BookModelOutput buildOutput(Book savedBook) {
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
        savedBook.getThumbnailUrl(),
        savedBook.getGoogleBookId(),
        savedBook.getCreatedAt(),
        savedBook.getUpdatedAt()
    );
  }
}
