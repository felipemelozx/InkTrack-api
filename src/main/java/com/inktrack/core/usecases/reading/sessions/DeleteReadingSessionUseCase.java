package com.inktrack.core.usecases.reading.sessions;

import java.util.UUID;

public interface DeleteReadingSessionUseCase {
  void execute(Long sessionId, UUID userId, Long bookId);
}
