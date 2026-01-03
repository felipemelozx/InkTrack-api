package com.inktrack.core.usecases.note;

import java.util.UUID;

public interface UpdateNoteUseCase {

  NoteOutput execute(Long noteId, UUID userId, NoteInput noteInput);

}
