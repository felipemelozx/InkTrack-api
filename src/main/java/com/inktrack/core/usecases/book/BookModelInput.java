package com.inktrack.core.usecases.book;

public record BookModelInput(
    String title,
    String author,
    int totalPages
) {
}
