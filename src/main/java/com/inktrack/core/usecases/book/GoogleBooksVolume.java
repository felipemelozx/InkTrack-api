package com.inktrack.core.usecases.book;

import java.util.List;

public record GoogleBooksVolume(
    String googleBooksId,
    String title,
    List<String> authors,
    Integer pageCount,
    String thumbnailUrl
) {
}
