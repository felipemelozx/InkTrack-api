package com.inktrack.infrastructure.dtos.notes;

import java.time.OffsetDateTime;

public record NoteResponse(
    Long id,
    Long bookId,
    String content,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
