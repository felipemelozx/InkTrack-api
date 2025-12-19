package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.persistence.BookRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
@Transactional
class BookControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    bookRepository.deleteAll();
    userRepository.deleteAll();
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

  @Test
  @DisplayName("Should create book successfully when authenticated and data is valid")
  void shouldCreateBookSuccessfully() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464);

    mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.title").value("Clean Code"))
        .andExpect(jsonPath("$.data.author").value("Robert C. Martin"))
        .andExpect(jsonPath("$.data.totalPages").value(464));

    var books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Clean Code");
  }

  @Test
  @DisplayName("Should return Forbidden when not authenticated")
  void shouldReturnForbiddenWhenNotAuthenticated() throws Exception {
    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464);

    mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return Bad Request when title is missing")
  void shouldReturnBadRequestWhenTitleIsMissing() throws Exception {
    String token = authenticateAndGetToken();

    BookCreateRequest request = new BookCreateRequest("", "Robert C. Martin", 464);

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

    BookCreateRequest request = new BookCreateRequest("Clean Code", "", 464);

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

    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464);

    mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.title").value("Clean Code"))
        .andExpect(jsonPath("$.data.author").value("Robert C. Martin"))
        .andExpect(jsonPath("$.data.totalPages").value(464));

    var books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Clean Code");

    Long bookId = books.get(0).getId();
    BookCreateRequest updateRequest = new BookCreateRequest("Clean Code PDF", "Robert C. Martin", 500);

    mockMvc.perform(put("/books/" + bookId)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.title").value(updateRequest.title()))
        .andExpect(jsonPath("$.data.author").value(updateRequest.author()))
        .andExpect(jsonPath("$.data.totalPages").value(updateRequest.totalPages()));


    books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals(updateRequest.title());
    assert books.get(0).getAuthor().equals(updateRequest.author());
    assert books.get(0).getTotalPages().equals(updateRequest.totalPages());
  }

  @Test
  @DisplayName("Should trows BookNotFoundException when id is invalid")
  void shouldThrowsBookNotFoundException() throws Exception {
    String token = authenticateAndGetToken();
    UUID userId = userRepository.findAll().get(0).getId();
    BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert C. Martin", 464);

    mockMvc.perform(post("/books")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.title").value("Clean Code"))
        .andExpect(jsonPath("$.data.author").value("Robert C. Martin"))
        .andExpect(jsonPath("$.data.totalPages").value(464));

    var books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Clean Code");

    Long bookId = books.get(0).getId();
    BookCreateRequest updateRequest = new BookCreateRequest("Clean Code PDF", "Robert C. Martin", 500);
    Long invalidBookId = bookId + 1;
    mockMvc.perform(put("/books/" + invalidBookId)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.data").value(nullValue()))
        .andExpect(jsonPath("$.errors").value(notNullValue()))
        .andExpect(jsonPath("$.errors[0].field").value("id"))
        .andExpect(jsonPath("$.errors[0].message").value("Book not found with this id: " +  invalidBookId + " and user id: " + userId));


    books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals(request.title());
    assert books.get(0).getAuthor().equals(request.author());
    assert books.get(0).getTotalPages().equals(request.totalPages());
  }
}
