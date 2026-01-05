package com.inktrack.core.usecases.book;

public record GetBookFilter(
    int page,
    int size,
    String title,
    Long categoryId,
    OrderEnum orderEnum
) {
}
