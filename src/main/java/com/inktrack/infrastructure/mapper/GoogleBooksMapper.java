package com.inktrack.infrastructure.mapper;

import com.inktrack.core.usecases.book.GoogleBooksVolume;
import com.inktrack.core.usecases.book.SearchBooksOutput;
import com.inktrack.infrastructure.dtos.book.BookSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class GoogleBooksMapper {

  public BookSearchResponse toSearchResponse(SearchBooksOutput output) {
    return new BookSearchResponse(
        output.totalItems(),
        output.volumes().stream()
            .map(this::toBookSearchItem)
            .toList()
    );
  }

  private BookSearchResponse.BookSearchItem toBookSearchItem(GoogleBooksVolume volume) {
    String author = volume.authors() != null
        ? String.join(", ", volume.authors())
        : "";

    return new BookSearchResponse.BookSearchItem(
        volume.googleBooksId(),
        volume.title(),
        author,
        volume.pageCount(),
        volume.thumbnailUrl()
    );
  }
}
