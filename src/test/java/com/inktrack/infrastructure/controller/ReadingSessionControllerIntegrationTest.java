package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.reading.session.ReadingSessionCreateRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.entity.ReadingSessionEntity;
import com.inktrack.infrastructure.persistence.BookRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReadingSessionControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;


  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReadingSessionRepository readingSessionRepository;

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
    readingSessionRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  private String authenticateAndGetToken() throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("Test User", "testReadingControlelr@email.com", "Password123!");
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    LoginRequest loginRequest = new LoginRequest("testReadingControlelr@email.com", "Password123!");
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

  @Test
  void shouldCreateReadingSessionSuccessfully() throws Exception {
    String accessToken = authenticateAndGetToken();
    long bookId = createBook(accessToken, new BookCreateRequest("The Pragmatic Programmer", "Andrew Hunt", 352));

    ReadingSessionCreateRequest request =
        new ReadingSessionCreateRequest(45L, 30);

    mockMvc.perform(
            post("/books/{bookId}/reading-sessions", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.minutes").value(45))
        .andExpect(jsonPath("$.data.pagesRead").value(30));

    var sessions = readingSessionRepository.findAll();

    assertThat(sessions).hasSize(1);

    ReadingSessionEntity session = sessions.get(0);

    assertThat(session.getMinutes()).isEqualTo(45L);
    assertThat(session.getPagesRead()).isEqualTo(30);
    assertThat(session.getBook().getId()).isEqualTo(bookId);
  }

  @Test
  @DisplayName("should return the reading session paginated")
  void shouldReturnReadingSessionPaginated() throws Exception {
    String accessToken = authenticateAndGetToken();
    long bookId = createBook(accessToken, new BookCreateRequest("Clean Code", "Robert C. Martin", 464));

    ReadingSessionCreateRequest request1 = new ReadingSessionCreateRequest(60L, 50);
    ReadingSessionCreateRequest request2 = new ReadingSessionCreateRequest(30L, 20);

    mockMvc.perform(
            post("/books/{bookId}/reading-sessions", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(request1))
        )
        .andExpect(status().isCreated());

    mockMvc.perform(
            post("/books/{bookId}/reading-sessions", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(request2))
        )
        .andExpect(status().isCreated());

    mockMvc.perform(
            get("/books/{bookId}/reading-sessions", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.data.length()").value(2))
        .andExpect(jsonPath("$.data.data[0].minutes").value(30))
        .andExpect(jsonPath("$.data.data[0].pagesRead").value(20))
        .andExpect(jsonPath("$.data.data[1].minutes").value(60))
        .andExpect(jsonPath("$.data.data[1].pagesRead").value(50));
  }
}
