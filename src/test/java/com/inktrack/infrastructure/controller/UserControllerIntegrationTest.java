package com.inktrack.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.user.ChangePasswordRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.DeleteAccountRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.dtos.user.UpdateUserRequest;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.persistence.CategoryRepository;
import com.inktrack.infrastructure.persistence.NoteRepository;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

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
  }

  private String authenticateAndGetToken(String email) throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("Test User", email, "Password123!");
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    LoginRequest loginRequest = new LoginRequest(email, "Password123!");
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
  void shouldGetCurrentUserProfile() throws Exception {
    String token = authenticateAndGetToken("profile@example.com");

    mockMvc.perform(get("/users/me")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.name").value("Test User"))
        .andExpect(jsonPath("$.data.email").value("profile@example.com"))
        .andExpect(jsonPath("$.data.createdAt").value(notNullValue()));
  }

  @Test
  void shouldReturnForbiddenWhenGettingProfileWithoutToken() throws Exception {
    mockMvc.perform(get("/users/me"))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldUpdateNameOnlyWithoutCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("update-name@example.com");

    mockMvc.perform(put("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of("name", "New Name"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("New Name"))
        .andExpect(jsonPath("$.data.email").value("update-name@example.com"));
  }

  @Test
  void shouldUpdateEmailWithCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("update-email@example.com");

    UpdateUserRequest request = new UpdateUserRequest(
        "Test User", "new-email@example.com", "Password123!"
    );

    mockMvc.perform(put("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value("new-email@example.com"))
        .andExpect(jsonPath("$.data.name").value("Test User"));
  }

  @Test
  void shouldNotUpdateEmailWithWrongCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("wrong-password@example.com");

    UpdateUserRequest request = new UpdateUserRequest(
        "Test User", "new-email@example.com", "WrongPassword1!"
    );

    mockMvc.perform(put("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldNotUpdateEmailWithoutCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("missing-password@example.com");

    UpdateUserRequest request = new UpdateUserRequest(
        "Test User", "new-email@example.com", null
    );

    mockMvc.perform(put("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("currentPassword"));
  }

  @Test
  void shouldNotUpdateEmailToAnotherUsersEmail() throws Exception {
    String token = authenticateAndGetToken("conflict-owner@example.com");
    authenticateAndGetToken("conflict-target@example.com");

    UpdateUserRequest request = new UpdateUserRequest(
        "Test User", "conflict-target@example.com", "Password123!"
    );

    mockMvc.perform(put("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldChangePasswordWithCorrectCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("change-password@example.com");

    ChangePasswordRequest request = new ChangePasswordRequest("Password123!", "NewPassword123!");

    mockMvc.perform(put("/users/me/password")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    LoginRequest newLogin = new LoginRequest("change-password@example.com", "NewPassword123!");
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newLogin)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value(notNullValue()));
  }

  @Test
  void shouldNotChangePasswordWithWrongCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("wrong-current@example.com");

    ChangePasswordRequest request = new ChangePasswordRequest("WrongPassword1!", "NewPassword123!");

    mockMvc.perform(put("/users/me/password")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldNotChangePasswordWithWeakNewPassword() throws Exception {
    String token = authenticateAndGetToken("weak-new@example.com");

    ChangePasswordRequest request = new ChangePasswordRequest("Password123!", "weak");

    mockMvc.perform(put("/users/me/password")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("newPassword"));
  }

  @Test
  void shouldDeleteAccountWithCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("delete-account@example.com");

    DeleteAccountRequest request = new DeleteAccountRequest("Password123!");

    mockMvc.perform(delete("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    assert userRepository.findByEmail("delete-account@example.com").isEmpty();
  }

  @Test
  void shouldNotDeleteAccountWithWrongCurrentPassword() throws Exception {
    String token = authenticateAndGetToken("delete-wrong@example.com");

    DeleteAccountRequest request = new DeleteAccountRequest("WrongPassword1!");

    mockMvc.perform(delete("/users/me")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    assert userRepository.findByEmail("delete-wrong@example.com").isPresent();
  }
}