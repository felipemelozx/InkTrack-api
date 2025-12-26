package com.inktrack.infrastructure.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.dtos.user.RefreshTokenRequest;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    userRepository.deleteAll();
  }

  @Test
  void shouldRegisterUserAndPersistToDatabase() throws Exception {
    CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "Password123!");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(notNullValue()))
        .andExpect(jsonPath("$.data.name").value("John Doe"))
        .andExpect(jsonPath("$.data.email").value("john@example.com"))
        .andExpect(jsonPath("$.data.createdAt").value(notNullValue()));

    var users = userRepository.findAll();
    assert users.size() == 1;
    assert users.get(0).getEmail().equals("john@example.com");
    assert users.get(0).getName().equals("John Doe");
  }

  @Test
  void shouldNotRegisterUserWithDuplicateEmail() throws Exception {
    CreateUserRequest firstRequest = new CreateUserRequest("John Doe", "john@example.com", "Password123!");
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(firstRequest)));

    CreateUserRequest duplicateRequest = new CreateUserRequest("Jane Doe", "john@example.com", "Password456!");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(duplicateRequest)))
        .andExpect(status().isConflict());

    var users = userRepository.findAll();
    assert users.size() == 1;
  }

  @Test
  void shouldLoginWithValidCredentials() throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("John Doe", "john@example.com", "Password123!");
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)));

    LoginRequest loginRequest = new LoginRequest("john@example.com", "Password123!");

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value(notNullValue()))
        .andExpect(jsonPath("$.data.refreshToken").value(notNullValue()));
  }

  @Test
  void shouldNotLoginWithInvalidCredentials() throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("John Doe", "john@example.com", "Password123!");
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)));

    LoginRequest loginRequest = new LoginRequest("john@example.com", "WrongPassword1!");

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldNotLoginWithNonExistentUser() throws Exception {
    LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "Password123!");

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldRefreshTokenSuccessfully() throws Exception {
    CreateUserRequest registerRequest = new CreateUserRequest("John Doe", "john@example.com", "Password123!");
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)));

    LoginRequest loginRequest = new LoginRequest("john@example.com", "Password123!");
    String loginResponse = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String refreshToken = objectMapper.readTree(loginResponse)
        .get("data")
        .get("refreshToken")
        .asText();

    RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);

    mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value(notNullValue()))
        .andExpect(jsonPath("$.data.refreshToken").value(notNullValue()));
  }

  @Test
  void shouldReturnBadRequestWhenRegisterWithEmptyName() throws Exception {
    CreateUserRequest request = new CreateUserRequest("", "john@example.com", "Password123!");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors[0].field").value("name"));
  }

  @Test
  void shouldReturnBadRequestWhenRegisterWithInvalidEmail() throws Exception {
    CreateUserRequest request = new CreateUserRequest("John Doe", "invalid-email", "Password123!");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors[0].field").value("email"));
  }

  @Test
  void shouldReturnBadRequestWhenRegisterWithWeakPassword() throws Exception {
    CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "123");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors[0].field").value("password"));
  }

  @Test
  void shouldReturnBadRequestWithMultipleValidationErrors() throws Exception {
    CreateUserRequest request = new CreateUserRequest("", "invalid-email", "weak");

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors.length()").value(3));
  }

  @Test
  void shouldReturnUnauthorizedWhenRefreshWithInvalidTokenStructure() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest("invalid.jwt.token");

    mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errors[0].field").value("token"))
        .andExpect(jsonPath("$.message").value("Invalid token format"));
  }

  @Test
  void shouldReturnUnauthorizedWhenRefreshWithGarbageToken() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest("not-a-valid-jwt");

    mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errors[0].field").value("token"))
        .andExpect(jsonPath("$.message").value("Invalid token format"));
  }

  @Test
  void shouldReturnUnauthorizedWhenTokenFailsSignatureVerification() throws Exception {
    Algorithm wrongAlgorithm = Algorithm.HMAC256("wrong-secret");

    String fakeValidToken = JWT.create()
        .withSubject("123")
        .withClaim("type", "refresh")
        .withExpiresAt(new Date(System.currentTimeMillis() + 10000))
        .sign(wrongAlgorithm);

    RefreshTokenRequest request = new RefreshTokenRequest(fakeValidToken);

    mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid or expired token"))
        .andExpect(jsonPath("$.errors[0].field").value("token"))
        .andExpect(jsonPath("$.errors[0].message").value("Invalid or expired token"));
  }
}
