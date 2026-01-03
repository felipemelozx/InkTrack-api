package com.inktrack.core.usecases.note;

import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.NoteGateway;

import java.util.UUID;

public class DeleteNoteUseCaseImpl implements DeleteNoteUseCase {

  private final NoteGateway noteGateway;

  public DeleteNoteUseCaseImpl(NoteGateway noteGateway) {
    this.noteGateway = noteGateway;
  }

  @Override
  public void execute(Long noteId, Long bookID, UUID userId) {
    boolean deleted = noteGateway.deleteNote(bookID, noteId, userId) > 0;

    if (!deleted) {
      throw new ResourceNotFoundException(
          "Note",
          "compositedId",
          String.format(
              "Note not found with noteId=%d, bookId=%d, userId=%s",
              noteId, bookID, userId
          )
      );
    }
  }
}
