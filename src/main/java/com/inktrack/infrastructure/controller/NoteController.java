package com.inktrack.infrastructure.controller;

import com.inktrack.core.usecases.note.CreateNoteUseCase;
import com.inktrack.core.usecases.note.DeleteNoteUseCase;
import com.inktrack.core.usecases.note.GetNotePaginatorUseCase;
import com.inktrack.core.usecases.note.NoteInput;
import com.inktrack.core.usecases.note.NoteOutput;
import com.inktrack.core.usecases.note.UpdateNoteUseCase;
import com.inktrack.core.utils.PageResult;
import com.inktrack.infrastructure.dtos.notes.CreateNoteRequest;
import com.inktrack.infrastructure.dtos.notes.NoteResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.NoteMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/{bookId}/notes")
public class NoteController {

  private final CreateNoteUseCase createNoteUseCase;
  private final GetNotePaginatorUseCase getNotePaginatorUseCase;
  private final UpdateNoteUseCase updateNoteUseCase;
  private final DeleteNoteUseCase deleteNoteUseCase;
  private final NoteMapper noteMapper;

  public NoteController(
      CreateNoteUseCase createNoteUseCase,
      GetNotePaginatorUseCase getNotePaginatorUseCase,
      UpdateNoteUseCase updateNoteUseCase,
      DeleteNoteUseCase deleteNoteUseCase,
      NoteMapper noteMapper
  ) {
    this.createNoteUseCase = createNoteUseCase;
    this.getNotePaginatorUseCase = getNotePaginatorUseCase;
    this.updateNoteUseCase = updateNoteUseCase;
    this.deleteNoteUseCase = deleteNoteUseCase;
    this.noteMapper = noteMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<NoteResponse>> createNote(
      @PathVariable Long bookId,
      @Valid @RequestBody CreateNoteRequest createNoteRequest,
      @AuthenticationPrincipal UserEntity currentUser
    ) {
    NoteInput noteInput = new NoteInput(bookId, createNoteRequest.content());
    NoteOutput noteOutput = createNoteUseCase.execute(currentUser.getId(), noteInput);
    NoteResponse noteResponse = noteMapper.outputToResponse(noteOutput);
    ApiResponse<NoteResponse> apiResponse = ApiResponse.success(noteResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResult<NoteResponse>>> getNotes(
      @PathVariable Long bookId,
      @RequestParam(defaultValue = "0") int page,
      @AuthenticationPrincipal UserEntity currentUser
    ) {
    PageResult<NoteOutput> outputPageResult = getNotePaginatorUseCase.execute(bookId, currentUser.getId(), page);
    PageResult<NoteResponse> responsePageResult = new PageResult<>(
        outputPageResult.pageSize(),
        outputPageResult.totalPages(),
        outputPageResult.currentPage(),
        outputPageResult.data().stream().map(noteMapper::outputToResponse).toList());
    return ResponseEntity.ok(ApiResponse.success(responsePageResult));
  }

  @PutMapping("/{noteId}")
  public ResponseEntity<ApiResponse<NoteResponse>> updateNote(
      @PathVariable Long bookId,
      @PathVariable Long noteId,
      @Valid @RequestBody CreateNoteRequest updateNoteRequest,
      @AuthenticationPrincipal UserEntity currentUser
    ) {
    NoteInput noteInput = new NoteInput(bookId, updateNoteRequest.content());
    NoteOutput noteOutput = updateNoteUseCase.execute(noteId, currentUser.getId(), noteInput);
    NoteResponse noteResponse = noteMapper.outputToResponse(noteOutput);
    ApiResponse<NoteResponse> apiResponse = ApiResponse.success(noteResponse);
    return ResponseEntity.ok(apiResponse);
  }

  @DeleteMapping("/{noteId}")
  public ResponseEntity<Void> delete(
      @PathVariable Long bookId,
      @PathVariable Long noteId,
      @AuthenticationPrincipal UserEntity currentUser
    ) {
    deleteNoteUseCase.execute(noteId, bookId, currentUser.getId());
    return ResponseEntity.noContent().build();
  }
}
