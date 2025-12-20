package com.inktrack.infrastructure.controller;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
import com.inktrack.core.usecases.book.BookModelOutput;
import com.inktrack.core.usecases.book.CreateBookUseCase;
import com.inktrack.core.usecases.book.UpdateBookUseCase;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.book.BookResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

  private final CreateBookUseCase createBookUseCase;
  private final UpdateBookUseCase updateBookUseCase;
  private final BookMapper bookMapper;
  private final UserMapper userMapper;

  public BookController(
      CreateBookUseCase createBookUseCase,
      UpdateBookUseCase updateBookUseCase,
      BookMapper bookMapper,
      UserMapper userMapper
  ) {
    this.createBookUseCase = createBookUseCase;
    this.updateBookUseCase = updateBookUseCase;
    this.bookMapper = bookMapper;
    this.userMapper = userMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookCreateRequest request,
                                                          @AuthenticationPrincipal UserEntity currentUser) {

    BookModelInput modelInput = bookMapper.requestDtoToModelInput(request);
    User userLogged = userMapper.entityToDomain(currentUser);
    BookModelOutput bookSaved = createBookUseCase.execute(modelInput, userLogged);
    BookResponse response = bookMapper.modelOutPutToResponse(bookSaved);
    ApiResponse<BookResponse> body = ApiResponse.success(response);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BookResponse>> update(
      @PathVariable Long id,
      @Valid @RequestBody BookCreateRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    BookModelInput modelInput = bookMapper.requestDtoToModelInput(request);
    BookModelOutput bookUpdated = updateBookUseCase.execute(id, modelInput, currentUser.getId());
    BookResponse response = bookMapper.modelOutPutToResponse(bookUpdated);
    ApiResponse<BookResponse> body = ApiResponse.success(response);
    return ResponseEntity.ok(body);
  }



}
