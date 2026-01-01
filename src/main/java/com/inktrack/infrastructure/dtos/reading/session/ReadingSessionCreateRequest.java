package com.inktrack.infrastructure.dtos.reading.session;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReadingSessionCreateRequest(
    @Positive
    @NotNull
    Long minutes,
    @Positive
    @NotNull
    Integer pagesRead
) {
}
