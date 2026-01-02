package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.NoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

  @Query("""
          SELECT n
          FROM NoteEntity n
          WHERE n.book.id = :bookId
          AND n.book.user.id = :userId
      """)
  Page<NoteEntity> findByBookIdAndUserId(
      @Param("bookId") Long bookId,
      @Param("userId") UUID userId,
      Pageable pageable
  );
}
