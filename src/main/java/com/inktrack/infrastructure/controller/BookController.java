package com.inktrack.infrastructure.controller;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
import com.inktrack.core.usecases.book.CreateBookUseCase;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.book.BookResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

  private final CreateBookUseCase createBookUseCase;
  private final BookMapper bookMapper;
  private final UserMapper userMapper;
  private final BookRepository book;

  public BookController(CreateBookUseCase createBookUseCase, BookMapper bookMapper, UserMapper userMapper, BookRepository book) {
    this.createBookUseCase = createBookUseCase;
    this.bookMapper = bookMapper;
    this.userMapper = userMapper;
    this.book = book;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookCreateRequest request,
                                                          @AuthenticationPrincipal UserEntity currentUser) {

    BookModelInput modelInput = bookMapper.requestDtoToModelInput(request);
    User userLogged = userMapper.entityToDomain(currentUser);
    Book bookSaved = createBookUseCase.execute(modelInput, userLogged);
    BookResponse response = bookMapper.domainToResponse(bookSaved);
    ApiResponse<BookResponse> body = ApiResponse.success(response);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }
}
