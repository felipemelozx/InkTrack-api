package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.notes.CreateNoteRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.persistence.NoteRepository;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NoteControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private NoteRepository noteRepository;

  @BeforeEach
  void setup() {
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
    bookRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  private String authenticateAndGetToken() throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("Test User", "testIntegrationNote@email.com", "Password123!");
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    LoginRequest loginRequest = new LoginRequest("testIntegrationNote@email.com", "Password123!");
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

  private long createNote(String token, CreateNoteRequest request) throws Exception {
    String createNoteResponse = mockMvc.perform(
            post("/notes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").isNotEmpty())
        .andExpect(jsonPath("$.data.content").value(request.content()))
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper.readTree(createNoteResponse)
        .get("data")
        .get("id")
        .asLong();
  }

  @Test
  @DisplayName("Should create the note when data request is valid")
  void shouldCreateNoteWhenDataIsValid() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest bookRequest = new BookCreateRequest("Effective Java", "Joshua Bloch", 416);
    long bookId = createBook(token, bookRequest);

    CreateNoteRequest noteRequest = new CreateNoteRequest(bookId, "This book is essential for every Java developer.");

    mockMvc.perform(post("/notes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.data.bookId").value(bookId))
        .andExpect(jsonPath("$.data.content").value(noteRequest.content()));
  }

  @Test
  @DisplayName("Should return 400 Bad Request with custom structure when content is blank")
  void shouldReturnBadRequest_WhenContentIsBlank() throws Exception {
    String token = authenticateAndGetToken();

    CreateNoteRequest request = new CreateNoteRequest(1L, "   ");

    mockMvc.perform(post("/notes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("content"))
        .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when content length exceeds 255 characters")
  void shouldReturnBadRequest_WhenContentLengthExceedsLimit() throws Exception {
    String token = authenticateAndGetToken();

    String longContent = "a".repeat(256);
    CreateNoteRequest request = new CreateNoteRequest(1L, longContent);

    mockMvc.perform(post("/notes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors[0].field").value("content"))
        .andExpect(jsonPath("$.errors[0].message").value("size must be between 0 and 255"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when bookId is null")
  void shouldReturnBadRequest_WhenBookIdIsNull() throws Exception {
    String token = authenticateAndGetToken();

    CreateNoteRequest request = new CreateNoteRequest(null, "Some content");

    mockMvc.perform(post("/notes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors[0].field").value("bookId"))
        .andExpect(jsonPath("$.errors[0].message").value("must not be null"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when bookId is zero or negative")
  void shouldReturnBadRequest_WhenBookIdIsNotPositive() throws Exception {
    String token = authenticateAndGetToken();

    CreateNoteRequest request = new CreateNoteRequest(0L, "Some content");

    mockMvc.perform(post("/notes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors[0].field").value("bookId"))
        .andExpect(jsonPath("$.errors[0].message").value("must be greater than 0"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request listing multiple errors when both fields are invalid")
  void shouldReturnBadRequest_WhenMultipleFieldsAreInvalid() throws Exception {
    String token = authenticateAndGetToken();

    CreateNoteRequest request = new CreateNoteRequest(0L, "");

    mockMvc.perform(post("/notes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors.length()").value(2))
        .andExpect(jsonPath("$.errors[*].field").value(containsInAnyOrder("bookId", "content")));
  }

  @Test
  @DisplayName("Should return paginated notes successfully for a specific book")
  void shouldGetNotesPaginatedSuccessfully() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest bookRequest = new BookCreateRequest("Domain-Driven Design", "Eric Evans", 400);
    long bookId = createBook(token, bookRequest);

    createNote(token, new CreateNoteRequest(bookId, "Note about aggregates"));
    createNote(token, new CreateNoteRequest(bookId, "Note about repositories"));

    mockMvc.perform(get("/notes")
            .header("Authorization", "Bearer " + token)
            .param("bookId", String.valueOf(bookId))
            .param("page", "0")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.data", hasSize(2)))
        .andExpect(jsonPath("$.data.currentPage").value(0))
        .andExpect(jsonPath("$.data.data[0].bookId").value(bookId))
        .andExpect(jsonPath("$.data.data[1].content").value("Note about aggregates"));
  }

  @Test
  @DisplayName("Should return empty list when book has no notes")
  void shouldReturnEmptyListWhenBookHasNoNotes() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest bookRequest = new BookCreateRequest("Clean Code", "Robert Martin", 464);
    long bookId = createBook(token, bookRequest);

    mockMvc.perform(get("/notes")
            .header("Authorization", "Bearer " + token)
            .param("bookId", String.valueOf(bookId))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.data", hasSize(0)));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when bookId parameter is missing")
  void shouldReturnBadRequestWhenBookIdIsMissing() throws Exception {
    String token = authenticateAndGetToken();

    mockMvc.perform(get("/notes")
                .header("Authorization", "Bearer " + token)
            // bookId não é enviado
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 Bad Request when bookId is not a valid number")
  void shouldReturnBadRequestWhenBookIdIsInvalid() throws Exception {
    String token = authenticateAndGetToken();

    mockMvc.perform(get("/notes")
            .header("Authorization", "Bearer " + token)
            .param("bookId", "invalid-number")
        )
        .andExpect(status().isBadRequest());
  }
}