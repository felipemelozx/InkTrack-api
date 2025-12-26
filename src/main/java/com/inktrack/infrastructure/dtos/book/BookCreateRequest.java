package com.inktrack.infrastructure.dtos.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BookCreateRequest(
    @NotBlank
    String title,
    @NotBlank
    String author,
    @Positive
    int totalPages
) {
}
