package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.ReadingSessionGateway;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class UpdateReadingSessionUseCaseImpl implements UpdateReadingSessionUseCase {

  private final ReadingSessionGateway readingSessionGateway;
  private final BookGateway bookGateway;

  public UpdateReadingSessionUseCaseImpl(ReadingSessionGateway readingSessionGateway, BookGateway bookGateway) {
    this.readingSessionGateway = readingSessionGateway;
    this.bookGateway = bookGateway;
  }

  @Override
  public ReadingSessionOutput execute(Long bookId, UUID userId, Long readingSessionId, ReadingSessionInput input) {
    Book book = bookGateway.findByIdAndUserId(bookId, userId);
    Optional<ReadingSession> readingSession = readingSessionGateway
        .getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId);

    if (readingSession.isEmpty()) {
      String message = "Reading session not found with id: " + readingSessionId;
      throw new ResourceNotFoundException("reading-session", "id", message);
    }

    book.removePagesRead(readingSession.get().getPagesRead());
    book.addPagesRead(input.pagesRead());
    bookGateway.update(book);
    ReadingSession updatedReadingSession = new ReadingSession.Builder()
        .id(readingSession.get().getId())
        .book(book)
        .pagesRead(input.pagesRead())
        .minutes(input.minutes())
        .sessionDate(OffsetDateTime.now())
        .build();

    readingSessionGateway.update(updatedReadingSession);
    return new ReadingSessionOutput(
        readingSession.get().getId(),
        book.getId(),
        updatedReadingSession.getMinutes(),
        updatedReadingSession.getPagesRead(),
        updatedReadingSession.getSessionDate()
    );
  }
}
