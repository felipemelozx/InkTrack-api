package com.inktrack.infrastructure.controller;

import com.inktrack.core.usecases.reading.sessions.CreateReadingSessionUseCase;
import com.inktrack.core.usecases.reading.sessions.ReadingSessionInput;
import com.inktrack.core.usecases.reading.sessions.ReadingSessionOutput;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionCreateRequest;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.ReadingSessionMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/{bookId}/reading-sessions")
public class ReadingSessionController {

  private final CreateReadingSessionUseCase createReadingSessionUseCase;
  private final ReadingSessionMapper readingSessionMapper;

  public ReadingSessionController(
      CreateReadingSessionUseCase createReadingSessionUseCase,
      ReadingSessionMapper readingSessionMapper
  ) {
    this.createReadingSessionUseCase = createReadingSessionUseCase;
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
            readingSessionMapper.inputToResponse(output)
        ));
  }
}
