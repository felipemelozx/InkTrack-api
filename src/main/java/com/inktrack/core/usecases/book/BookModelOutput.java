package com.inktrack.core.usecases.book;

import com.inktrack.core.usecases.user.UserOutput;

import java.time.OffsetDateTime;

public record BookModelOutput(
    Long id,
    UserOutput user,
    String title,
    String author,
    int totalPages,
    int pagesRead,
    int progress,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
