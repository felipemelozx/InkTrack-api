package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
}
