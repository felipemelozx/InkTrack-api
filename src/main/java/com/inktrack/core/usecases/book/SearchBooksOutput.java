package com.inktrack.core.usecases.book;

import java.util.List;

public record SearchBooksOutput(
    int totalItems,
    List<GoogleBooksVolume> volumes
) {
}
