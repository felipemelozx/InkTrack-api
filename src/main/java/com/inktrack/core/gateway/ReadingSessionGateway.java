package com.inktrack.core.gateway;

import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.utils.PageResult;

import java.util.UUID;

public interface ReadingSessionGateway {

  ReadingSession save(ReadingSession readingSession);

  PageResult<ReadingSession> getReadingByBookIdAndUserId(Long bookId, UUID userId, int page, int size);
}
