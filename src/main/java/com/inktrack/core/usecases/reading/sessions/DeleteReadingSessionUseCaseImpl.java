package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.ReadingSessionGateway;

import java.util.UUID;

public class DeleteReadingSessionUseCaseImpl implements DeleteReadingSessionUseCase {

  private final ReadingSessionGateway readingSessionGateway;
  private final BookGateway bookGateway;

  public DeleteReadingSessionUseCaseImpl(ReadingSessionGateway readingSessionGateway, BookGateway bookGateway) {
    this.readingSessionGateway = readingSessionGateway;
    this.bookGateway = bookGateway;
  }

  @Override
  public void execute(Long sessionId, UUID userId, Long bookId) {
    Book book = bookGateway.findByIdAndUserId(bookId, userId);
    ReadingSession readingSession = readingSessionGateway
        .getByIdAndByBookIdAndUserId(sessionId, bookId, userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "ReadingSession",
            "compositeId",
            String.format(
                "sessionId=%d, bookId=%d, userId=%s",
                sessionId, bookId, userId
            )
        ));


    int rows = readingSessionGateway.deleteReadingSession(sessionId, userId, bookId);
    if (rows == 0) {
      throw new ResourceNotFoundException(
          "ReadingSession",
          "compositeId",
          String.format(
              "sessionId=%d, bookId=%d, userId=%s",
              sessionId, bookId, userId
          )
      );
    }

    book.removePagesRead(readingSession.getPagesRead());
    bookGateway.update(book);
  }
}
