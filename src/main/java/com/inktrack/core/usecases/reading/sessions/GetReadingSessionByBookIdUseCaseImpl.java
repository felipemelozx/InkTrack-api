package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.utils.PageResult;

import java.util.UUID;

public class GetReadingSessionByBookIdUseCaseImpl implements GetReadingSessionByBookIdUseCase {

  private final ReadingSessionGateway readingSessionGateway;

  public GetReadingSessionByBookIdUseCaseImpl(ReadingSessionGateway readingSessionGateway) {
    this.readingSessionGateway = readingSessionGateway;
  }

  @Override
  public PageResult<ReadingSessionOutput> execute(Long bookId, UUID userId, int page) {
    int fixedPageSize = 3;
    PageResult<ReadingSession> sessionPageResult = readingSessionGateway
        .getReadingByBookIdAndUserId(bookId, userId, page, fixedPageSize);

    return new PageResult<>(
        sessionPageResult.pageSize(),
        sessionPageResult.totalPages(),
        sessionPageResult.currentPage(),
        sessionPageResult.data().stream()
            .map(session -> new ReadingSessionOutput(
                session.getId(),
                session.getBook().getId(),
                session.getMinutes(),
                session.getPagesRead(),
                session.getSessionDate()
            ))
            .toList()
    );
  }
}
