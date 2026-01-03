package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.Note;
import com.inktrack.core.gateway.NoteGateway;
import com.inktrack.core.utils.PageResult;
import com.inktrack.infrastructure.entity.NoteEntity;
import com.inktrack.infrastructure.mapper.NoteMapper;
import com.inktrack.infrastructure.persistence.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class NoteGatewayImpl implements NoteGateway {

  private final NoteRepository noteRepository;
  private final NoteMapper noteMapper;

  public NoteGatewayImpl(NoteRepository noteRepository, NoteMapper noteMapper) {
    this.noteRepository = noteRepository;
    this.noteMapper = noteMapper;
  }

  @Override
  public Note save(Note note) {
    NoteEntity noteEntity = noteMapper.domainToEntity(note);
    return noteMapper.entityToDomain(noteRepository.save(noteEntity));
  }

  @Override
  public Note update(Note note) {
    return save(note);
  }

  @Override
  public PageResult<Note> getNotesByBooidAndUserId(Long bookId, UUID userId, int page) {
    Pageable pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "updatedAt"));
    Page<NoteEntity> pageEntity = noteRepository.findByBookIdAndUserId(bookId, userId, pageRequest);

    return new PageResult<>(
        5,
        pageEntity.getTotalPages(),
        page,
        pageEntity.getContent().stream()
            .map(noteMapper::entityToDomain)
            .toList()
    );
  }

  @Override
  public Optional<Note> getNoteByIdAndBookIdAndUserId(Long bookId, Long noteId, UUID userId) {
    Optional<NoteEntity> entityOptional = noteRepository.findByNoteIdBookIdUserId(noteId, bookId, userId);
    return entityOptional.map(noteMapper::entityToDomain);
  }

  @Override
  public int deleteNote(Long bookID, Long noteId, UUID userId) {
    return noteRepository.deleteByIdBookIdUserId(noteId, bookID, userId);
  }
}
