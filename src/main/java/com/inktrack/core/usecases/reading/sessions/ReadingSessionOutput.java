package com.inktrack.core.usecases.reading.sessions;

import java.time.OffsetDateTime;

public record ReadingSessionOutput(
    Long id,
    Long bookId,
    Long minutes,
    Integer pagesRead,
    OffsetDateTime sessionDate
) {
}
