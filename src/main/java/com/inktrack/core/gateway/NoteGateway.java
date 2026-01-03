package com.inktrack.core.gateway;

import com.inktrack.core.domain.Note;
import com.inktrack.core.utils.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface NoteGateway {

  Note save(Note note);

  Note update(Note note);

  PageResult<Note> getNotesByBooidAndUserId(Long bookId, UUID userId, int page);

  Optional<Note> getNoteByIdAndBookIdAndUserId(Long bookId, Long noteId, UUID userId);

  int deleteNote(Long bookID, Long noteId, UUID userId);
}
