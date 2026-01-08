package com.inktrack.core.usecases.book;

import com.inktrack.core.gateway.GoogleBooksGateway;

public class SearchBooksUseCaseImpl implements SearchBooksUseCase {

  private final GoogleBooksGateway googleBooksGateway;

  public SearchBooksUseCaseImpl(GoogleBooksGateway googleBooksGateway) {
    this.googleBooksGateway = googleBooksGateway;
  }

  @Override
  public SearchBooksOutput execute(String query) {
    if (query == null || query.isBlank()) {
      throw new IllegalArgumentException("Query cannot be null or empty");
    }

    if (query.length() > 500) {
      throw new IllegalArgumentException("Query too long (max 500 characters)");
    }

    return googleBooksGateway.searchBooks(query.trim());
  }
}
