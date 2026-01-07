package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.notes.CreateNoteRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private ReadingSessionRepository readingSessionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long testCategoryId;

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
        readingSessionRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        CategoryEntity category = categoryRepository.save(new CategoryEntity(null, "Fiction", java.time.OffsetDateTime.now()));
        testCategoryId = category.getId();
    }

    private String authenticateAndGetToken() throws Exception {
        CreateUserRequest registerRequest = new CreateUserRequest("Test User", "testIntegrationNote@email.com",
                "Password123!");
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

    private long createNote(String token, Long bookId, CreateNoteRequest request) throws Exception {
        String createNoteResponse = mockMvc.perform(
                post("/books/" + bookId + "/notes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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

        BookCreateRequest bookRequest = new BookCreateRequest("Effective Java", "Joshua Bloch", 416, testCategoryId);
        long bookId = createBook(token, bookRequest);

        CreateNoteRequest noteRequest = new CreateNoteRequest("This book is essential for every Java developer.");

        mockMvc.perform(post("/books/" + bookId + "/notes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isCreated())
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

        CreateNoteRequest request = new CreateNoteRequest("   ");

        mockMvc.perform(post("/books/1/notes")
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
        CreateNoteRequest request = new CreateNoteRequest(longContent);

        mockMvc.perform(post("/books/1/notes")
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
    @DisplayName("Should return paginated notes successfully for a specific book")
    void shouldGetNotesPaginatedSuccessfully() throws Exception {
        String token = authenticateAndGetToken();

        BookCreateRequest bookRequest = new BookCreateRequest("Domain-Driven Design", "Eric Evans", 400, testCategoryId);
        long bookId = createBook(token, bookRequest);

        createNote(token, bookId, new CreateNoteRequest("Note about aggregates"));
        createNote(token, bookId, new CreateNoteRequest("Note about repositories"));

        mockMvc.perform(get("/books/" + bookId + "/notes")
                .header("Authorization", "Bearer " + token)
                .param("page", "0"))
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

        BookCreateRequest bookRequest = new BookCreateRequest("Clean Code", "Robert Martin", 464, testCategoryId);
        long bookId = createBook(token, bookRequest);

        mockMvc.perform(get("/books/" + bookId + "/notes")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should update note successfully when request is valid")
    void shouldUpdateNoteSuccessfully() throws Exception {
        String token = authenticateAndGetToken();

        BookCreateRequest bookRequest = new BookCreateRequest("Refactoring", "Martin Fowler", 450, testCategoryId);
        long bookId = createBook(token, bookRequest);

        long noteId = createNote(
                token,
                bookId,
                new CreateNoteRequest("Initial content"));

        CreateNoteRequest updateRequest = new CreateNoteRequest("Updated note content");

        mockMvc.perform(
                put("/books/" + bookId + "/notes/" + noteId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(noteId))
                .andExpect(jsonPath("$.data.bookId").value(bookId))
                .andExpect(jsonPath("$.data.content").value("Updated note content"))
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }

    @Test
    @DisplayName("Should return 404 when updating a non-existing note")
    void shouldReturnNotFoundWhenNoteDoesNotExist() throws Exception {
        String token = authenticateAndGetToken();

        CreateNoteRequest updateRequest = new CreateNoteRequest("Updated content");

        mockMvc.perform(
                put("/books/1/notes/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating note with blank content")
    void shouldReturnBadRequestWhenUpdatingWithBlankContent() throws Exception {
        String token = authenticateAndGetToken();

        BookCreateRequest bookRequest = new BookCreateRequest("Clean Architecture", "Robert Martin", 350, testCategoryId);
        long bookId = createBook(token, bookRequest);

        long noteId = createNote(
                token,
                bookId,
                new CreateNoteRequest("Valid content"));

        CreateNoteRequest updateRequest = new CreateNoteRequest("   ");

        mockMvc.perform(
                put("/books/" + bookId + "/notes/" + noteId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("content"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating note with content exceeding 255 chars")
    void shouldReturnBadRequestWhenUpdatingWithLongContent() throws Exception {
        String token = authenticateAndGetToken();

        BookCreateRequest bookRequest = new BookCreateRequest("Java Concurrency", "Brian Goetz", 420, testCategoryId);
        long bookId = createBook(token, bookRequest);

        long noteId = createNote(
                token,
                bookId,
                new CreateNoteRequest("Initial content"));

        String longContent = "a".repeat(256);
        CreateNoteRequest updateRequest = new CreateNoteRequest(longContent);

        mockMvc.perform(
                put("/books/" + bookId + "/notes/" + noteId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].field").value("content"))
                .andExpect(jsonPath("$.errors[0].message").value("size must be between 0 and 255"));
    }

    @Test
    @DisplayName("Should delete note successfully when it exists")
    void shouldDeleteNoteSuccessfully() throws Exception {
        String token = authenticateAndGetToken();

        BookCreateRequest bookRequest = new BookCreateRequest("Design Patterns", "Gang of Four", 395, testCategoryId);
        long bookId = createBook(token, bookRequest);

        long noteId = createNote(
                token,
                bookId,
                new CreateNoteRequest("Singleton pattern note"));

        mockMvc.perform(
                delete("/books/" + bookId + "/notes/" + noteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/" + bookId + "/notes")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting non-existing note")
    void shouldReturnNotFoundWhenDeletingNonExistingNote() throws Exception {
        String token = authenticateAndGetToken();

        BookCreateRequest bookRequest = new BookCreateRequest("Test Book", "Test Author", 100, testCategoryId);
        long bookId = createBook(token, bookRequest);

        mockMvc.perform(
                delete("/books/" + bookId + "/notes/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

}