package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.BookEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
  @Query("SELECT b FROM BookEntity b WHERE b.user.id = :userId AND b.id = :id")
  Optional<BookEntity> findByIdAndUserId(@Param("id") Long id,@Param("userId") UUID userId);

  @Query("""
        SELECT b
        FROM BookEntity b
        WHERE b.user.id = :userId
          AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:categoryId IS NULL OR b.category.id = :categoryId)
      """)
  List<BookEntity> getUserBookPage(
      @Param("userId") UUID userId,
      @Param("title") String title,
      @Param("categoryId") Long categoryId,
      Pageable pageable
  );

  @Query("""
        SELECT COUNT(b)
        FROM BookEntity b
        WHERE b.user.id = :userId
          AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:categoryId IS NULL OR b.category.id = :categoryId)
      """)
  long countUserBooks(
      @Param("userId") UUID userId,
      @Param("title") String title,
      @Param("categoryId") Long categoryId
  );

  @Modifying
  @Query("""
        DELETE FROM BookEntity b
        WHERE b.id = :bookId
          AND b.user.id = :userId
      """)
  int deleteByIdAndUserId(@Param("bookId") Long bookId, @Param("userId") UUID userId);

  @Query("SELECT COUNT(b) FROM BookEntity b WHERE b.user.id = :userId")
  int countTotalBooksByUserId(@Param("userId") UUID userId);

  @Query("SELECT AVG(b.progress) FROM BookEntity b WHERE b.user.id = :userId")
  Double getAverageProgressByUserId(@Param("userId") UUID userId);

  @Query("SELECT SUM(b.totalPages - b.pagesRead) FROM BookEntity b WHERE b.user.id = :userId")
  Integer getTotalPagesRemainingByUserId(@Param("userId") UUID userId);

  @Query("""
      SELECT c.name as category, COUNT(b) as count
      FROM BookEntity b
      JOIN b.category c
      WHERE b.user.id = :userId
      GROUP BY c.name
      ORDER BY COUNT(b) DESC
      """)
  List<CategoryBookCountProjection> getBooksCountByCategory(@Param("userId") UUID userId);
}
