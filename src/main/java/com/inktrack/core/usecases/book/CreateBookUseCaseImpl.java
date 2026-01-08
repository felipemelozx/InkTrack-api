package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.core.gateway.GoogleBooksGateway;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;

import java.util.Optional;

public class CreateBookUseCaseImpl implements CreateBookUseCase {

  private final BookGateway bookGateway;
  private final CategoryGateway categoryGateway;
  private final GoogleBooksGateway googleBooksGateway;

  public CreateBookUseCaseImpl(
      BookGateway bookGateway,
      CategoryGateway categoryGateway,
      GoogleBooksGateway googleBooksGateway
  ) {
    this.bookGateway = bookGateway;
    this.categoryGateway = categoryGateway;
    this.googleBooksGateway = googleBooksGateway;
  }

  @Override
  public BookModelOutput execute(BookModelInput modelInput, User currentUser) {
    validateInput(modelInput, currentUser);
    Category category = categoryGateway.getById(modelInput.categoryId())
        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + modelInput.categoryId()));

    String thumbnailUrl = fetchThumbnailFromGoogleBooks(modelInput.googleBookId());
    Book book = createBook(modelInput, currentUser, category, thumbnailUrl);
    Book bookSaved = bookGateway.save(book);

    return buildOutput(bookSaved);
  }

  private void validateInput(BookModelInput modelInput, User currentUser) {
    if (currentUser == null) {
      throw new IllegalArgumentException("User not logged in");
    }
    if(modelInput.totalPages() < 0) {
      throw new IllegalArgumentException("The total pages must be greater than zero.");
    }
    if(modelInput.title().isBlank() || modelInput.author().isBlank()) {
      throw new IllegalArgumentException("The title and author not can be black or null.");
    }
  }

  private Book createBook(BookModelInput modelInput, User currentUser, Category category, String thumbnailUrl) {
    return Book.builder()
        .user(currentUser)
        .category(category)
        .title(modelInput.title())
        .author(modelInput.author())
        .totalPages(modelInput.totalPages())
        .thumbnailUrl(thumbnailUrl)
        .build();
  }

  private BookModelOutput buildOutput(Book bookSaved) {
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
        bookSaved.getThumbnailUrl(),
        bookSaved.getCreatedAt(),
        bookSaved.getUpdatedAt()
    );
  }

  private String fetchThumbnailFromGoogleBooks(String googleBookId) {
    if (googleBookId == null || googleBookId.isBlank()) {
      return null;
    }

    try {
      Optional<GoogleBooksVolume> volume = googleBooksGateway.getVolumeById(googleBookId);
      return volume.map(GoogleBooksVolume::thumbnailUrl).orElse(null);
    } catch (Exception e) {
      System.err.println("Failed to fetch book thumbnail from Google Books: " + e.getMessage());
      return null;
    }
  }
}
