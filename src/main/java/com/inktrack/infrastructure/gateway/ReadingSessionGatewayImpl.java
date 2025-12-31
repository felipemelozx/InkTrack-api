package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.utils.PageResult;
import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import com.inktrack.infrastructure.mapper.ReadingSessionMapper;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ReadingSessionGatewayImpl implements ReadingSessionGateway {

  private final ReadingSessionRepository readingSessionRepository;
  private final ReadingSessionMapper readingSessionMapper;

  public ReadingSessionGatewayImpl(
      ReadingSessionRepository readingSessionRepository,
      ReadingSessionMapper readingSessionMapper
  ) {
    this.readingSessionRepository = readingSessionRepository;
    this.readingSessionMapper = readingSessionMapper;
  }

  @Override
  public ReadingSession save(ReadingSession readingSession) {
    ReadingSessionEntity readingSessionEntity = readingSessionMapper
        .domainToEntity(readingSession);
    ReadingSessionEntity readingSessionSaved = readingSessionRepository.save(readingSessionEntity);
    return readingSessionMapper.entityToDomain(readingSessionSaved);
  }

  @Override
  public PageResult<ReadingSession> getReadingByBookIdAndUserId(Long bookId, UUID userId, int page, int size) {
    Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(Sort.Direction.DESC, "sessionDate")
    );

    Page<ReadingSessionEntity> readingSessionEntityPage = readingSessionRepository
        .getReadingSession(bookId, userId, pageable);

    return new PageResult<>(
        size,
        readingSessionEntityPage.getTotalPages(),
        page,
        readingSessionEntityPage.getContent()
            .stream()
            .map(readingSessionMapper::entityToDomain)
            .toList()
    );
  }

  @Override
  public Optional<ReadingSession> getByIdAndByBookIdAndUserId(Long readingSessionId, Long bookId, UUID userId) {
    Optional<ReadingSessionEntity> optionalEntity = readingSessionRepository
        .getByIdAndByBookIdAndUserId(readingSessionId, bookId, userId);
    return optionalEntity.map(readingSessionMapper::entityToDomain);
  }

  @Override
  public ReadingSession update(ReadingSession readingSession) {
    return save(readingSession);
  }

}
