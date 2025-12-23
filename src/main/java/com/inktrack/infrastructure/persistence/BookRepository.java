package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.BookEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
      """)
  List<BookEntity> getUserBookPage(@Param("userId") UUID userId, @Param("title") String titlte, Pageable pageable);

  @Query("SELECT COUNT(b) FROM BookEntity b WHERE b.user.id = :userId")
  long countUserBooks(@Param("userId") UUID userId);
}
