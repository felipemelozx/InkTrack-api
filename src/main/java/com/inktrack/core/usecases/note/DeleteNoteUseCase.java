package com.inktrack.core.usecases.note;

import java.util.UUID;

public interface DeleteNoteUseCase {
  void execute(Long noteId, Long bookID, UUID userId);
}
