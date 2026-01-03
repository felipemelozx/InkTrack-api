package com.inktrack.core.usecases.note;

import java.time.OffsetDateTime;

public record NoteOutput(
    Long id,
    Long bookId,
    String content,
    OffsetDateTime createAt,
    OffsetDateTime updatedAt
) {
}
