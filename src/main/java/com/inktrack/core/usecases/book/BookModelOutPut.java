package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.User;

import java.time.OffsetDateTime;

public record BookModelOutPut(
    Long id,
    User user,
    String title,
    String author,
    int totalPages,
    int pagesRead,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
