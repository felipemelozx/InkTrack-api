package com.inktrack.infrastructure.dtos.reading.session;

import jakarta.validation.constraints.Positive;

public record ReadingSessionCreateRequest(
    @Positive
    Long minutes,
    @Positive
    Integer pagesRead
) {
}
