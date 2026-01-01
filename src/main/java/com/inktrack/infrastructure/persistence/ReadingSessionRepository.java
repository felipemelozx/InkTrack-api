package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReadingSessionRepository extends JpaRepository<ReadingSessionEntity, Long> {

  @Query("""
          SELECT r
          FROM ReadingSessionEntity r
          WHERE r.book.id = :bookId
          AND r.book.user.id = :userId
      """)
  Page<ReadingSessionEntity> getReadingSession(@Param("bookId") Long bookId,
                                               @Param("userId") UUID userId,
                                               Pageable pageable);

  @Query("""
          SELECT r
          FROM ReadingSessionEntity r
          WHERE r.id = :readingId
          AND r.book.id = :bookId
          AND r.book.user.id = :userId
      """)
  Optional<ReadingSessionEntity> getByIdAndByBookIdAndUserId(
      @Param("readingId") Long readingSessionId,
      @Param("bookId") Long bookId,
      @Param("userId") UUID userId
  );

  @Modifying
  @Query("""
          DELETE FROM ReadingSessionEntity r
          WHERE r.id = :readingId
          AND r.book.user.id = :userId
          AND r.book.id = :bookId
      """)
  int deleteByIdAndUserId(@Param("readingId") Long readingSessionId,
                          @Param("bookId") Long bookId,
                          @Param("userId") UUID userId);
}
