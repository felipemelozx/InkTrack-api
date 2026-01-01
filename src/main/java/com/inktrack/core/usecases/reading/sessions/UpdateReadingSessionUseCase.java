package com.inktrack.core.usecases.reading.sessions;

import java.util.UUID;

public interface UpdateReadingSessionUseCase {

  ReadingSessionOutput execute(Long bookId, UUID userId, Long readingSessionId, ReadingSessionInput input);

}
