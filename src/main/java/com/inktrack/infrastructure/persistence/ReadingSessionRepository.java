package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
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

  @Query("SELECT COUNT(r) FROM ReadingSessionEntity r WHERE r.book.user.id = :userId")
  int countTotalSessionsByUserId(@Param("userId") UUID userId);

  @Query("SELECT SUM(r.minutes) FROM ReadingSessionEntity r WHERE r.book.user.id = :userId")
  Long getTotalMinutesByUserId(@Param("userId") UUID userId);

  @Query("""
      SELECT AVG(CAST(r.pagesRead AS DOUBLE) / NULLIF(r.minutes, 0))
      FROM ReadingSessionEntity r
      WHERE r.book.user.id = :userId
        AND r.minutes > 0
      """)
  Double getAveragePagesPerMinuteByUserId(@Param("userId") UUID userId);

  @Query("SELECT AVG(r.pagesRead) FROM ReadingSessionEntity r WHERE r.book.user.id = :userId")
  Double getAveragePagesPerSessionByUserId(@Param("userId") UUID userId);

  @Query("""
      SELECT r.sessionDate as date, COALESCE(SUM(r.pagesRead), 0) as totalPages
      FROM ReadingSessionEntity r
      WHERE r.book.user.id = :userId
        AND r.sessionDate >= :startDate
      GROUP BY r.sessionDate
      ORDER BY r.sessionDate ASC
      """)
  List<ReadingEvolutionProjection> getReadingEvolution(
      @Param("userId") UUID userId,
      @Param("startDate") LocalDate startDate
  );
}
