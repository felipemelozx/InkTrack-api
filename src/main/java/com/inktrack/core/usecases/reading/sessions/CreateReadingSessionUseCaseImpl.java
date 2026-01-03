package com.inktrack.core.usecases.reading.sessions;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.ReadingSessionGateway;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateReadingSessionUseCaseImpl implements CreateReadingSessionUseCase {

  private final ReadingSessionGateway readingSessionGateway;
  private final BookGateway bookGateway;

  public CreateReadingSessionUseCaseImpl(ReadingSessionGateway readingSessionGateway, BookGateway bookGateway) {
    this.readingSessionGateway = readingSessionGateway;
    this.bookGateway = bookGateway;
  }

  @Override
  public ReadingSessionOutput execute(ReadingSessionInput input, UUID userId) {
    if (userId == null) {
      throw new FieldDomainValidationException("userId", "The user id can't be null.");
    }

    Book book = bookGateway.findByIdAndUserId(input.bookId(), userId);

    ReadingSession readingSession = ReadingSession.builder()
        .book(book)
        .minutes(input.minutes())
        .pagesRead(input.pagesRead())
        .sessionDate(OffsetDateTime.now())
        .build();

    book.addPagesRead(input.pagesRead());
    bookGateway.update(book);

    ReadingSession readingSessionSaved = readingSessionGateway.save(readingSession);
    return new ReadingSessionOutput(
        readingSessionSaved.getId(),
        book.getId(),
        readingSessionSaved.getMinutes(),
        readingSessionSaved.getPagesRead(),
        readingSessionSaved.getSessionDate()
    );
  }
}
