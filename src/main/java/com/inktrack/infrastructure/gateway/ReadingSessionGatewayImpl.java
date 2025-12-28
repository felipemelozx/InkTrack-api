package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.ReadingSession;
import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import com.inktrack.infrastructure.mapper.ReadingSessionMapper;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import org.springframework.stereotype.Component;

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

}
