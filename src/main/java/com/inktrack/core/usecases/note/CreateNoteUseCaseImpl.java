package com.inktrack.core.usecases.note;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Note;
import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.NoteGateway;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateNoteUseCaseImpl implements CreateNoteUseCase {

  private final NoteGateway noteGateway;
  private final BookGateway bookGateway;

  public CreateNoteUseCaseImpl(NoteGateway noteGateway, BookGateway bookGateway) {
    this.noteGateway = noteGateway;
    this.bookGateway = bookGateway;
  }

  @Override
  public NoteOutput execute(UUID userId, NoteInput noteInput) {
    validInput(noteInput);
    Book book = bookGateway.findByIdAndUserId(noteInput.bookId(), userId);

    Note note = new Note(book, noteInput.content(), OffsetDateTime.now(), OffsetDateTime.now());
    Note noteSaved = noteGateway.save(note);
    return new NoteOutput(
        noteSaved.getId(),
        noteSaved.getBook().getId(),
        noteSaved.getContent(),
        noteSaved.getCreatedAt(),
        noteSaved.getUpdatedAt()
    );
  }


  private void validInput(NoteInput noteInput) {
    if (noteInput.content().isBlank()) {
      throw new FieldDomainValidationException("content", "The content can't be blank.");
    }
    if (noteInput.content().length() > 255) {
      throw new FieldDomainValidationException("content", "The content can't be greater than 255 characters.");
    }
    if (noteInput.bookId() == null) {
      throw new FieldDomainValidationException("bookId", "The book id can't be null.");
    }
  }
}
