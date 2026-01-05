package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.CategoryEntity;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.persistence.CategoryRepository;
import com.inktrack.infrastructure.persistence.NoteRepository;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
class BookControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReadingSessionRepository readingRepository;

  @Autowired
  private NoteRepository noteRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  private Long testCategoryId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
  }

  @BeforeEach
  void cleanDatabase() {
    noteRepository.deleteAllInBatch();
    readingRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    categoryRepository.deleteAllInBatch();

    // Create default category for tests
    CategoryEntity category = categoryRepository.save(new CategoryEntity(null, "Fiction", java.time.OffsetDateTime.now()));
    testCategoryId = category.getId();
  }

  private String authenticateAndGetToken() throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("Test User", "test@email.com", "Password123!");
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    LoginRequest loginRequest = new LoginRequest("test@email.com", "Password123!");
    String loginResponse = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper.readTree(loginResponse)
        .get("data")
        .get("accessToken")
        .asText();
  }

  private long createBook(String token, BookCreateRequest request) throws Exception {
    String createBookResponse = mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").isNotEmpty())
        .andExpect(jsonPath("$.data.title").value(request.title()))
        .andExpect(jsonPath("$.data.author").value(request.author()))
        .andExpect(jsonPath("$.data.totalPages").value(request.totalPages()))
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper
        .readTree(createBookResponse)
        .get("data")
        .get("id")
        .asLong();
  }

  private long createBook(String token, String title) throws Exception {
    BookCreateRequest request =
        new BookCreateRequest(title, "Robert C. Martin", 464, testCategoryId);

    String createBookResponse = mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper
        .readTree(createBookResponse)
        .get("data")
        .get("id")
        .asLong();
  }

  @Test
  @DisplayName("Should create book successfully when authenticated and data is valid")
  void shouldCreateBookSuccessfully() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464, testCategoryId);

    createBook(token, request);

    var books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Clean Code");
  }

  @Test
  @DisplayName("Should return Forbidden when not authenticated")
  void shouldReturnForbiddenWhenNotAuthenticated() throws Exception {
    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464, testCategoryId);

    mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return Bad Request when title is missing")
  void shouldReturnBadRequestWhenTitleIsMissing() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest request = new BookCreateRequest("", "Robert C. Martin", 464, testCategoryId);

    mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return Bad Request when author is missing")
  void shouldReturnBadRequestWhenAuthorIsMissing() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest request = new BookCreateRequest("Clean Code", "", 464, testCategoryId);

    mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should update book successfully when authenticated and data is valid")
  void shouldUpdateBookSuccessfully() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464, testCategoryId);

    createBook(token, request);

    List<BookEntity> books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Clean Code");

    Long bookId = books.get(0).getId();
    BookCreateRequest updateRequest = new BookCreateRequest("Clean Code PDF", "Robert C. Martin", 500, testCategoryId);

    mockMvc.perform(put("/books/" + bookId)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.title").value(updateRequest.title()))
        .andExpect(jsonPath("$.data.author").value(updateRequest.author()))
        .andExpect(jsonPath("$.data.totalPages").value(updateRequest.totalPages()));


    var book = bookRepository.findById(bookId);
    assert book.get().getTitle().equals(updateRequest.title());
    assert book.get().getAuthor().equals(updateRequest.author());
    assert book.get().getTotalPages().equals(updateRequest.totalPages());
  }

  @Test
  @DisplayName("Should trows BookNotFoundException when id is invalid")
  void shouldThrowsBookNotFoundException() throws Exception {
    String token = authenticateAndGetToken();
    UUID userId = userRepository.findAll().get(0).getId();
    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464, testCategoryId);

    createBook(token, request);

    List<BookEntity> books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Clean Code");

    Long bookId = books.get(0).getId();
    BookCreateRequest updateRequest = new BookCreateRequest("Clean Code PDF", "Robert C. Martin", 500, testCategoryId);
    Long invalidBookId = bookId + 1;
    mockMvc.perform(put("/books/" + invalidBookId)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data").value(nullValue()))
        .andExpect(jsonPath("$.errors").value(notNullValue()))
        .andExpect(jsonPath("$.errors[0].field").value("id"))
        .andExpect(jsonPath("$.errors[0].message").value("Book not found with this id: " + invalidBookId + " and user id: " + userId));


    books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals(request.title());
    assert books.get(0).getAuthor().equals(request.author());
    assert books.get(0).getTotalPages().equals(request.totalPages());
  }

  @Test
  @DisplayName("Should return books with default pagination and sorting")
  void shouldReturnBooksWithDefaultFilters() throws Exception {
    String token = authenticateAndGetToken();
    BookCreateRequest request =
        new BookCreateRequest("Clean Code", "Robert C. Martin", 464, testCategoryId);

    createBook(token, request);
    createBook(token, request);

    mockMvc.perform(get("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.totalPages").value(1))
        .andExpect(jsonPath("$.data.currentPage").value(0));

    List<BookEntity> books = bookRepository.findAll();

    assertThat(books)
        .hasSize(2)
        .allSatisfy(book -> {
          assertThat(book.getTitle()).isEqualTo(request.title());
          assertThat(book.getAuthor()).isEqualTo(request.author());
          assertThat(book.getTotalPages()).isEqualTo(request.totalPages());
        });
  }

  @Test
  @DisplayName("Should filter books by title")
  void shouldFilterBooksByTitle() throws Exception {
    String token = authenticateAndGetToken();

    createBook(token, "Clean Code");
    createBook(token, "Domain Driven Design");

    mockMvc.perform(get("/books")
            .param("title", "Clean")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data.length()").value(1))
        .andExpect(jsonPath("$.data.data[0].title").value("Clean Code"));
  }

  @Test
  @DisplayName("Should return books ordered by most recent")
  void shouldReturnBooksOrderedByRecent() throws Exception {
    String token = authenticateAndGetToken();

    createBook(token, "Book 1");
    createBook(token, "Book 2");

    mockMvc.perform(get("/books")
            .param("sortBy", "RECENT")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].title").value("Book 2"));
  }

  @Test
  @DisplayName("Should return books ordered by oldest")
  void shouldReturnBooksOrderedByOldest() throws Exception {
    String token = authenticateAndGetToken();

    createBook(token, "Book 1");
    createBook(token, "Book 2");

    mockMvc.perform(get("/books")
            .param("sortBy", "OLDEST")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].title").value("Book 1"));
  }

  @Test
  @DisplayName("Should return paginated books")
  void shouldReturnPaginatedBooks() throws Exception {
    String token = authenticateAndGetToken();

    createBook(token, "Book 1");
    createBook(token, "Book 2");
    createBook(token, "Book 3");

    mockMvc.perform(get("/books")
            .param("page", "0")
            .param("size", "2")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data.length()").value(2))
        .andExpect(jsonPath("$.data.totalPages").value(2));
  }

  @Test
  @DisplayName("Should return book with id valid")
  void shouldReturnBookWithValidId() throws Exception {
    String token = authenticateAndGetToken();
    BookCreateRequest request = new BookCreateRequest("Clean code", "Unclo bob", 400, testCategoryId);

    Long bookId = createBook(token, request);

    mockMvc.perform(get("/books/{id}", bookId)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value(request.title()))
        .andExpect(jsonPath("$.data.id").value(bookId))
        .andExpect(jsonPath("$.data.author").value(request.author()));
  }

  @Test
  @DisplayName("Should return 404 when trying to access a book from another user")
  void shouldReturnNotFoundWhenAccessingBookFromAnotherUser() throws Exception {

    CreateUserRequest userARequest =
        new CreateUserRequest("User A", "usera@email.com", "Password123!");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userARequest)))
        .andExpect(status().isCreated());

    LoginRequest loginUserA =
        new LoginRequest("usera@email.com", "Password123!");

    String tokenUserA = objectMapper.readTree(
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginUserA)))
                .andReturn()
                .getResponse()
                .getContentAsString()
        )
        .get("data")
        .get("accessToken")
        .asText();

    BookCreateRequest bookRequest =
        new BookCreateRequest("Clean Code", "Robert C. Martin", 464, testCategoryId);

    Long bookId = createBook(tokenUserA, bookRequest);

    assertTrue(bookRepository.findById(bookId).isPresent());

    CreateUserRequest userBRequest =
        new CreateUserRequest("User B", "userb@email.com", "Password123!");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userBRequest)))
        .andExpect(status().isCreated());

    LoginRequest loginUserB =
        new LoginRequest("userb@email.com", "Password123!");

    String tokenUserB = objectMapper.readTree(
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginUserB)))
                .andReturn()
                .getResponse()
                .getContentAsString()
        )
        .get("data")
        .get("accessToken")
        .asText();

    UUID userBId = userRepository
        .findAll()
        .stream()
        .filter(u -> u.getEmail().equals("userb@email.com"))
        .findFirst()
        .get()
        .getId();

    mockMvc.perform(get("/books/{id}", bookId)
            .header("Authorization", "Bearer " + tokenUserB))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data").value(nullValue()))
        .andExpect(jsonPath("$.errors").isNotEmpty())
        .andExpect(jsonPath("$.errors[0].field").value("id"))
        .andExpect(jsonPath("$.errors[0].message")
            .value("Book not found with this id: " + bookId + " and user id: " + userBId));
  }

  @Test
  @DisplayName("Should delete book and remove it from database")
  void shouldDeleteBookWhenBookExists() throws Exception {
    String token = authenticateAndGetToken();

    Long bookId = createBook(token, "Book 1");
    assertNotNull(bookId);

    assertTrue(bookRepository.findById(bookId).isPresent());

    mockMvc.perform(delete("/books/{id}", bookId)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
    var bb = bookRepository.findById(bookId);
    assertTrue(bb.isEmpty());
  }

  @Test
  @DisplayName("Should return 404 when trying to delete a non-existing book")
  void shouldReturnNotFoundWhenDeletingNonExistingBook() throws Exception {
    String token = authenticateAndGetToken();
    Long invalidBookId = 1l;
    mockMvc.perform(delete("/books/{id}", invalidBookId)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data").value(nullValue()))
        .andExpect(jsonPath("$.errors").value(notNullValue()))
        .andExpect(jsonPath("$.errors[0].field").value("id"));
  }
}
