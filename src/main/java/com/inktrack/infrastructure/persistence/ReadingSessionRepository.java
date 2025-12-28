package com.inktrack.infrastructure.persistence;

import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingSessionRepository extends JpaRepository<ReadingSessionEntity, Long> {
}
