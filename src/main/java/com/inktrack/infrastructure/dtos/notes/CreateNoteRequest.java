package com.inktrack.infrastructure.dtos.notes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
    @NotNull
    @Positive
    Long bookId,
    @NotNull
    @NotBlank
    @Size(max = 255)
    String content
) {
}
