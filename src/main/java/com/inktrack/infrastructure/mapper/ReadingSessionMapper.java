package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.usecases.reading.sessions.ReadingSessionInput;
import com.inktrack.core.usecases.reading.sessions.ReadingSessionOutput;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionCreateRequest;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionResponse;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import org.springframework.stereotype.Component;

@Component
public class ReadingSessionMapper {

  private final BookMapper bookMapper;

  public ReadingSessionMapper(BookMapper bookMapper) {
    this.bookMapper = bookMapper;
  }

  public ReadingSessionEntity domainToEntity(ReadingSession readingSession) {
    BookEntity book = bookMapper.domainToEntity(readingSession.getBook());
    return new ReadingSessionEntity(
        book,
        readingSession.getPagesRead(),
        readingSession.getMinutes(),
        readingSession.getSessionDate()
    );
  }

  public ReadingSession entityToDomain(ReadingSessionEntity readingSessionSaved) {
    Book book = bookMapper.entityToDomain(readingSessionSaved.getBook());
    return ReadingSession.builder()
        .id(readingSessionSaved.getId())
        .book(book)
        .minutes(readingSessionSaved.getMinutes())
        .pagesRead(readingSessionSaved.getPagesRead())
        .sessionDate(readingSessionSaved.getSessionDate())
        .build();
  }

  public ReadingSessionInput requestToInput(ReadingSessionCreateRequest request, Long bookId) {
    return new ReadingSessionInput(bookId, request.minutes(), request.pagesRead());
  }

  public ReadingSessionResponse inputToResponse(ReadingSessionOutput output) {
    return new ReadingSessionResponse(
        output.id(),
        output.bookId(),
        output.minutes(),
        output.pagesRead(),
        output.sessionDate()
    );
  }
}
