package com.inktrack.core.usecases.note;

import java.util.UUID;

public interface CreateNoteUseCase {

  NoteOutput execute(UUID userId, NoteInput noteInput);

}
