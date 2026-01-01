package com.inktrack.infrastructure.dtos.reading.session;

import java.time.OffsetDateTime;

public record ReadingSessionResponse(
    Long id,
    Long bookId,
    Long minutes,
    Integer pagesRead,
    OffsetDateTime sessionDate
) {
}
