package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.ReadingSession;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import com.inktrack.infrastructure.mapper.ReadingSessionMapper;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadingSessionGatewayImpl")
class ReadingSessionGatewayImplTest {

  @Mock
  private ReadingSessionRepository readingSessionRepository;

  @Mock
  private ReadingSessionMapper readingSessionMapper;

  @InjectMocks
  private ReadingSessionGatewayImpl readingSessionGateway;


  private ReadingSession domain;
  private ReadingSessionEntity entity;
  private ReadingSessionEntity entitySaved;

  @BeforeEach
  void setUp() {
    OffsetDateTime sessionDate = OffsetDateTime.now();

    Book book = mock(Book.class);

    domain = ReadingSession.builder()
        .id(1L)
        .book(book)
        .pagesRead(20)
        .minutes(30L)
        .sessionDate(sessionDate)
        .build();

    BookEntity bookEntity = mock(BookEntity.class);

    entity = new ReadingSessionEntity(
        null,
        bookEntity,
        20,
        30L,
        sessionDate
    );

    entitySaved = new ReadingSessionEntity(
        null,
        bookEntity,
        20,
        30L,
        sessionDate
    );
  }

  @Test
  @DisplayName("Should save reading session and return mapped domain")
  void shouldSaveReadingSessionAndReturnMappedDomain() {
    when(readingSessionMapper.domainToEntity(domain))
        .thenReturn(entity);

    when(readingSessionRepository.save(entity))
        .thenReturn(entitySaved);

    ReadingSession mappedDomainSaved = ReadingSession.builder()
        .id(10L)
        .pagesRead(20)
        .book(mock(Book.class))
        .minutes(30L)
        .sessionDate(entitySaved.getSessionDate())
        .build();

    when(readingSessionMapper.entityToDomain(entitySaved))
        .thenReturn(mappedDomainSaved);

    ReadingSession result = readingSessionGateway.save(domain);

    assertNotNull(result);
    assertEquals(10L, result.getId());
    assertEquals(20, result.getPagesRead());
    assertEquals(30L, result.getMinutes());
    assertEquals(entitySaved.getSessionDate(), result.getSessionDate());

    verify(readingSessionMapper).domainToEntity(domain);
    verify(readingSessionRepository).save(entity);
    verify(readingSessionMapper).entityToDomain(entitySaved);
  }
}
