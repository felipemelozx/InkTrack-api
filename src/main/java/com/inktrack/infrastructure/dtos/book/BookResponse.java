package com.inktrack.infrastructure.dtos.book;

import com.inktrack.infrastructure.dtos.category.CategoryResponse;
import com.inktrack.infrastructure.dtos.user.UserResponse;

import java.time.OffsetDateTime;

public record BookResponse(
    Long id,
    UserResponse user,
    CategoryResponse category,
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
