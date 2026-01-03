package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Note;
import com.inktrack.core.usecases.note.NoteOutput;
import com.inktrack.infrastructure.dtos.notes.NoteResponse;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.NoteEntity;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

  private final BookMapper bookMapper;

  public NoteMapper(BookMapper bookMapper) {
    this.bookMapper = bookMapper;
  }

  public NoteEntity domainToEntity(Note note) {
    BookEntity bookEntity = bookMapper.domainToEntity(note.getBook());
    return new NoteEntity(
        note.getId(),
        bookEntity,
        note.getContent(),
        note.getCreatedAt(),
        note.getUpdatedAt());
  }

  public Note entityToDomain(NoteEntity noteEntity) {
    Book book = bookMapper.entityToDomain(noteEntity.getBook());
    return new Note(
        noteEntity.getId(),
        book,
        noteEntity.getContent(),
        noteEntity.getCreatedAt(),
        noteEntity.getUpdatedAt());
  }

  public NoteResponse outputToResponse(NoteOutput noteOutput) {
    return new NoteResponse(
        noteOutput.id(),
        noteOutput.bookId(),
        noteOutput.content(),
        noteOutput.createAt(),
        noteOutput.updatedAt());
  }
}
