package com.inktrack.core.usecases.reading.sessions;

public record ReadingSessionInput(
    Long bookId,
    Long minutes,
    Integer pagesRead
) {
}
