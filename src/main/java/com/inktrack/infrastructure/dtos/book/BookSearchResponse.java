package com.inktrack.infrastructure.dtos.book;

import java.util.List;

public record BookSearchResponse(
    int totalItems,
    List<BookSearchItem> volumes
) {

  public record BookSearchItem(
      String googleBooksId,
      String title,
      String author,
      Integer totalPages,
      String thumbnailUrl
  ) {
  }
}
