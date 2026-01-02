package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.Note;
import com.inktrack.core.gateway.NoteGateway;
import com.inktrack.infrastructure.entity.NoteEntity;
import com.inktrack.infrastructure.mapper.NoteMapper;
import com.inktrack.infrastructure.persistence.NoteRepository;
import org.springframework.stereotype.Component;

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
}
