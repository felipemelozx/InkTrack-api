package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
  @Query("SELECT b FROM BookEntity b WHERE b.user.id = :userId AND b.id = :id")
  Optional<BookEntity> findByIdAndUserId(@Param("id") Long id,@Param("userId") UUID userId);
}
