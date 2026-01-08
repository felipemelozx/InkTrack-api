package com.inktrack.core.usecases.book;

import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;

import java.time.OffsetDateTime;

public record BookModelOutput(
    Long id,
    UserOutput user,
    CategoryOutput category,
    String title,
    String author,
    int totalPages,
    int pagesRead,
    int progress,
    String thumbnailUrl,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
