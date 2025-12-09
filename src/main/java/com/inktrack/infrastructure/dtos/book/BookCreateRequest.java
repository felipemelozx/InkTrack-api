package com.inktrack.infrastructure.dtos.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record BookCreateRequest(
    @NotBlank
    String title,
    @NotBlank
    String author,
    @Positive
    int totalPages,
    @PositiveOrZero
    int pagesRead
) {
}
