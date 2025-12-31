package com.inktrack.infrastructure.controller;

import com.inktrack.core.usecases.reading.sessions.CreateReadingSessionUseCase;
import com.inktrack.core.usecases.reading.sessions.GetReadingSessionByBookIdUseCase;
import com.inktrack.core.usecases.reading.sessions.ReadingSessionInput;
import com.inktrack.core.usecases.reading.sessions.ReadingSessionOutput;
import com.inktrack.core.usecases.reading.sessions.UpdateReadingSessionUseCase;
import com.inktrack.core.utils.PageResult;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionCreateRequest;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.ReadingSessionMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/{bookId}/reading-sessions")
public class ReadingSessionController {

  private final CreateReadingSessionUseCase createReadingSessionUseCase;
  private final GetReadingSessionByBookIdUseCase getReadingSessionByBookIdUseCase;
  private final UpdateReadingSessionUseCase updateReadingSessionUseCase;
  private final ReadingSessionMapper readingSessionMapper;

  public ReadingSessionController(
      CreateReadingSessionUseCase createReadingSessionUseCase,
      GetReadingSessionByBookIdUseCase getReadingSessionByBookIdUseCase,
      UpdateReadingSessionUseCase updateReadingSessionUseCase,
      ReadingSessionMapper readingSessionMapper
  ) {
    this.createReadingSessionUseCase = createReadingSessionUseCase;
    this.getReadingSessionByBookIdUseCase = getReadingSessionByBookIdUseCase;
    this.updateReadingSessionUseCase = updateReadingSessionUseCase;
    this.readingSessionMapper = readingSessionMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ReadingSessionResponse>> create(
      @PathVariable Long bookId,
      @Valid @RequestBody ReadingSessionCreateRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    ReadingSessionInput input =
        readingSessionMapper.requestToInput(request, bookId);

    ReadingSessionOutput output =
        createReadingSessionUseCase.execute(input, currentUser.getId());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(
            readingSessionMapper.outputToResponse(output)
        ));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResult<ReadingSessionResponse>>> getReadingSessionsByBookId(
      @PathVariable Long bookId,
      @RequestParam(defaultValue = "0", required = false) int page,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    PageResult<ReadingSessionOutput> outputPageResult = getReadingSessionByBookIdUseCase
        .execute(bookId, currentUser.getId(), page);

    PageResult<ReadingSessionResponse> responsePageResult = new PageResult<>(
        outputPageResult.pageSize(),
        outputPageResult.totalPages(),
        outputPageResult.currentPage(),
        outputPageResult.data().stream()
            .map(readingSessionMapper::outputToResponse)
            .toList()
    );
    return ResponseEntity.ok().body(ApiResponse.success(responsePageResult));
  }

  @PutMapping("/{readingSessionId}")
  public ResponseEntity<ApiResponse<ReadingSessionResponse>> update(
      @Positive @PathVariable Long readingSessionId,
      @Positive @PathVariable Long bookId,
      @Valid @RequestBody ReadingSessionCreateRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    ReadingSessionInput input =
        readingSessionMapper.requestToInput(request, readingSessionId);

    ReadingSessionOutput output = updateReadingSessionUseCase
        .execute(bookId, currentUser.getId(), readingSessionId, input);

    return ResponseEntity.ok().body(ApiResponse.success(readingSessionMapper.outputToResponse(output)));
  }
}
