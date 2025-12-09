package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.User;

import java.time.LocalDateTime;

public record BookModelOutPut(
    Long id,
    User user,
    String title,
    String author,
    int totalPages,
    int pagesRead,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
