package com.inktrack.core.usecases.note;

import com.inktrack.core.domain.Note;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.NoteGateway;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class UpdateNoteUseCaseImpl implements UpdateNoteUseCase {

  private final NoteGateway noteGateway;

  public UpdateNoteUseCaseImpl(NoteGateway noteGateway) {
    this.noteGateway = noteGateway;
  }


  @Override
  public NoteOutput execute(Long noteId, UUID userId, NoteInput noteInput) {
    Optional<Note> optionalNote = noteGateway.getNoteByIdAndBookIdAndUserId(noteInput.bookId(), noteId, userId);

    if (optionalNote.isEmpty()) {
      throw new ResourceNotFoundException(
          "Note",
          "id",
          "Note with id " + noteId + " not found for the given book and user."
      );
    }

    Note noteToUpdate = optionalNote.get();
    Note updatedNote = new Note(
        noteToUpdate.getId(),
        noteToUpdate.getBook(),
        noteInput.content(),
        noteToUpdate.getCreatedAt(),
        OffsetDateTime.now()
    );
    Note savedNote = noteGateway.update(updatedNote);
    return new NoteOutput(
        savedNote.getId(),
        savedNote.getBook().getId(),
        savedNote.getContent(),
        savedNote.getCreatedAt(),
        savedNote.getUpdatedAt()
    );
  }
}
