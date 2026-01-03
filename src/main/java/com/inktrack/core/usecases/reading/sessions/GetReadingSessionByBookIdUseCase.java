package com.inktrack.core.usecases.reading.sessions;


import com.inktrack.core.utils.PageResult;

import java.util.UUID;

public interface GetReadingSessionByBookIdUseCase {

  PageResult<ReadingSessionOutput> execute(Long bookId, UUID userId, int page);

}
