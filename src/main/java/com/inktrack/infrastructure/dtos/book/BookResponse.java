package com.inktrack.infrastructure.dtos.book;

import com.inktrack.infrastructure.dtos.user.UserResponse;

import java.time.OffsetDateTime;

public record BookResponse(
    Long id,
    UserResponse user,
    String title,
    String author,
    int totalPages,
    int pagesRead,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
