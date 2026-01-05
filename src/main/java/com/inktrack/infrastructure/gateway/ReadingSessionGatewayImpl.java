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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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

  @Override
  @Transactional
  public int deleteReadingSession(Long sessionId, UUID userId, Long bookId) {
    return readingSessionRepository.deleteByIdAndUserId(sessionId, bookId, userId);
  }

  @Override
  public int getTotalSessionsByUserId(UUID userId) {
    return readingSessionRepository.countTotalSessionsByUserId(userId);
  }

  @Override
  public long getTotalMinutesByUserId(UUID userId) {
    Long totalMinutes = readingSessionRepository.getTotalMinutesByUserId(userId);
    return totalMinutes != null ? totalMinutes : 0L;
  }

  @Override
  public Double getAveragePagesPerMinuteByUserId(UUID userId) {
    Double avg = readingSessionRepository.getAveragePagesPerMinuteByUserId(userId);
    return avg != null ? avg : 0.0;
  }

  @Override
  public Double getAveragePagesPerSessionByUserId(UUID userId) {
    Double avg = readingSessionRepository.getAveragePagesPerSessionByUserId(userId);
    return avg != null ? avg : 0.0;
  }

  @Override
  public List<EvolutionData> getReadingEvolution(UUID userId, LocalDate startDate) {
    return readingSessionRepository.getReadingEvolution(userId, startDate).stream()
        .map(projection -> new EvolutionData(
            projection.getDate(),
            projection.getTotalPages() != null ? projection.getTotalPages() : 0
        ))
        .toList();
  }

}
