package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.entity.CategoryEntity;
import com.inktrack.infrastructure.entity.ReadingSessionEntity;
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

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
class MetricsControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ReadingSessionRepository readingSessionRepository;

  @Autowired
  private NoteRepository noteRepository;

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
    readingSessionRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    categoryRepository.deleteAllInBatch();

    CategoryEntity category = categoryRepository.save(
        new CategoryEntity(null, "Fiction", OffsetDateTime.now())
    );
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

  private long createBook(String token, String title, int totalPages) throws Exception {
    BookCreateRequest request = new BookCreateRequest(title, "Author Name", totalPages, testCategoryId);

    String createBookResponse = mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper.readTree(createBookResponse)
        .get("data")
        .get("id")
        .asLong();
  }

  private void createReadingSession(long bookId, int pagesRead, long minutes, OffsetDateTime date) {
    ReadingSessionEntity session = new ReadingSessionEntity(
        null,
        bookRepository.findById(bookId).orElseThrow(),
        pagesRead,
        minutes,
        date
    );
    readingSessionRepository.save(session);
  }

  @Test
  @DisplayName("Should return general metrics")
  void shouldReturnGeneralMetrics() throws Exception {
    String token = authenticateAndGetToken();

    createBook(token, "Book 1", 300);
    createBook(token, "Book 2", 400);
    createBook(token, "Book 3", 200);

    mockMvc.perform(get("/metrics")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.totalBooks").value(3))
        .andExpect(jsonPath("$.data.averageProgress").exists())
        .andExpect(jsonPath("$.data.totalPagesRemaining").exists())
        .andExpect(jsonPath("$.data.estimatedDaysToFinish").exists());
  }

  @Test
  @DisplayName("Should return reading session metrics")
  void shouldReturnReadingSessionMetrics() throws Exception {
    String token = authenticateAndGetToken();
    long bookId = createBook(token, "Book 1", 300);

    OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    createReadingSession(bookId, 20, 30L, now);
    createReadingSession(bookId, 25, 35L, now.minusDays(1));

    mockMvc.perform(get("/metrics/reading-sessions")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.totalSessions").value(2))
        .andExpect(jsonPath("$.data.totalMinutes").value(65))
        .andExpect(jsonPath("$.data.averagePagesPerMinute").exists())
        .andExpect(jsonPath("$.data.averagePagesPerSession").value(22.5));
  }

  @Test
  @DisplayName("Should return books by category")
  void shouldReturnBooksByCategory() throws Exception {
    String token = authenticateAndGetToken();

    createBook(token, "Book 1", 300);
    createBook(token, "Book 2", 400);

    mockMvc.perform(get("/metrics/categories")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.categories").isArray())
        .andExpect(jsonPath("$.data.categories.length()").value(1))
        .andExpect(jsonPath("$.data.categories[0].categoryName").value("Fiction"))
        .andExpect(jsonPath("$.data.categories[0].bookCount").value(2));
  }

  @Test
  @DisplayName("Should return reading evolution with default period")
  void shouldReturnReadingEvolutionWithDefaultPeriod() throws Exception {
    String token = authenticateAndGetToken();
    long bookId = createBook(token, "Book 1", 300);

    OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    createReadingSession(bookId, 20, 30L, now);
    createReadingSession(bookId, 15, 25L, now.minusDays(1));

    mockMvc.perform(get("/metrics/evolution")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.period").value("30d"))
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.data.length()").value(2));
  }

  @Test
  @DisplayName("Should return reading evolution with custom period")
  void shouldReturnReadingEvolutionWithCustomPeriod() throws Exception {
    String token = authenticateAndGetToken();
    long bookId = createBook(token, "Book 1", 300);

    OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    createReadingSession(bookId, 30, 40L, now);

    mockMvc.perform(get("/metrics/evolution")
            .header("Authorization", "Bearer " + token)
            .param("period", "3m"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.period").value("3m"))
        .andExpect(jsonPath("$.data.data").isArray());
  }

  @Test
  @DisplayName("Should return 403 when accessing metrics without authentication")
  void shouldReturn403WhenAccessingMetricsWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/metrics"))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return empty metrics for user with no books")
  void shouldReturnEmptyMetricsForUserWithNoBooks() throws Exception {
    String token = authenticateAndGetToken();

    mockMvc.perform(get("/metrics")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.totalBooks").value(0))
        .andExpect(jsonPath("$.data.averageProgress").value(0.0))
        .andExpect(jsonPath("$.data.totalPagesRemaining").value(0))
        .andExpect(jsonPath("$.data.estimatedDaysToFinish").value(0));
  }

  @Test
  @DisplayName("Should return empty reading session metrics for user with no sessions")
  void shouldReturnEmptyReadingSessionMetricsForUserWithNoSessions() throws Exception {
    String token = authenticateAndGetToken();

    mockMvc.perform(get("/metrics/reading-sessions")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.totalSessions").value(0))
        .andExpect(jsonPath("$.data.totalMinutes").value(0))
        .andExpect(jsonPath("$.data.averagePagesPerMinute").value(0.0))
        .andExpect(jsonPath("$.data.averagePagesPerSession").value(0.0));
  }
}