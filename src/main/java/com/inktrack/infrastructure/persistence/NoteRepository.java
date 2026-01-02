package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
}
