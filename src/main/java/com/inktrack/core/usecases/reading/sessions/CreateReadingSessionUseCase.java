package com.inktrack.core.usecases.reading.sessions;

import java.util.UUID;

public interface CreateReadingSessionUseCase {

  ReadingSessionOutput execute(ReadingSessionInput input, UUID userId);

}
