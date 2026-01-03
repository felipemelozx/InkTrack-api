package com.inktrack.core.usecases.note;

public record NoteInput(
    Long bookId,
    String content
) {
}
