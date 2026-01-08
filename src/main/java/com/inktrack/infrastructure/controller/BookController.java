package com.inktrack.infrastructure.controller;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
import com.inktrack.core.usecases.book.BookModelOutput;
import com.inktrack.core.usecases.book.CreateBookUseCase;
import com.inktrack.core.usecases.book.DeleteBookUseCase;
import com.inktrack.core.usecases.book.GetBookByIdUseCase;
import com.inktrack.core.usecases.book.GetBookFilter;
import com.inktrack.core.usecases.book.GetBooksUseCase;
import com.inktrack.core.usecases.book.OrderEnum;
import com.inktrack.core.usecases.book.SearchBooksOutput;
import com.inktrack.core.usecases.book.SearchBooksUseCase;
import com.inktrack.core.usecases.book.UpdateBookUseCase;
import com.inktrack.core.utils.PageResult;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.book.BookResponse;
import com.inktrack.infrastructure.dtos.book.BookSearchResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.mapper.GoogleBooksMapper;
import com.inktrack.infrastructure.mapper.UserMapper;
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

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

  private final CreateBookUseCase createBookUseCase;
  private final UpdateBookUseCase updateBookUseCase;
  private final GetBooksUseCase getBooksUseCase;
  private final GetBookByIdUseCase getBookByIdUseCase;
  private final DeleteBookUseCase deleteBookUseCase;
  private final SearchBooksUseCase searchBooksUseCase;
  private final BookMapper bookMapper;
  private final UserMapper userMapper;
  private final GoogleBooksMapper googleBooksMapper;

  public BookController(
      CreateBookUseCase createBookUseCase,
      UpdateBookUseCase updateBookUseCase,
      GetBooksUseCase getBooksUseCase,
      GetBookByIdUseCase getBookByIdUseCase,
      DeleteBookUseCase deleteBookUseCase,
      SearchBooksUseCase searchBooksUseCase,
      BookMapper bookMapper,
      UserMapper userMapper,
      GoogleBooksMapper googleBooksMapper
  ) {
    this.createBookUseCase = createBookUseCase;
    this.updateBookUseCase = updateBookUseCase;
    this.getBooksUseCase = getBooksUseCase;
    this.getBookByIdUseCase = getBookByIdUseCase;
    this.deleteBookUseCase = deleteBookUseCase;
    this.searchBooksUseCase = searchBooksUseCase;
    this.bookMapper = bookMapper;
    this.userMapper = userMapper;
    this.googleBooksMapper = googleBooksMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<BookResponse>> create(
      @Valid @RequestBody BookCreateRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
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

  @GetMapping
  public ResponseEntity<ApiResponse<PageResult<BookResponse>>> getBooks(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false, defaultValue = "") String title,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false, defaultValue = "RECENT") OrderEnum sortBy,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    GetBookFilter filter = new GetBookFilter(
        page,
        size,
        title,
        categoryId,
        sortBy
    );
    PageResult<BookModelOutput> books = getBooksUseCase.execute(currentUser.getId(), filter);
    List<BookResponse> bookResponseList = books.data()
        .stream()
        .map(bookMapper::modelOutPutToResponse)
        .toList();
    PageResult<BookResponse> dataResponse = new PageResult<>(
        books.pageSize(),
        books.totalPages(),
        books.currentPage(),
        bookResponseList
    );
    return ResponseEntity.ok(ApiResponse.success(dataResponse));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<BookSearchResponse>> searchBooks(
      @RequestParam String q
  ) {
    SearchBooksOutput result = searchBooksUseCase.execute(q);
    BookSearchResponse response = googleBooksMapper.toSearchResponse(result);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BookResponse>> getBookById(
      @PathVariable Long id,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    BookModelOutput bookModelOutput = getBookByIdUseCase.execute(id, currentUser.getId());
    BookResponse response = bookMapper.modelOutPutToResponse(bookModelOutput);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    deleteBookUseCase.execute(id, currentUser.getId());
    return ResponseEntity.noContent().build();
  }
}
