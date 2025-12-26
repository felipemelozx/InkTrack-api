package com.inktrack.infrastructure.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGatewayImplTest {

  private PasswordGatewayImpl passwordGateway;

  @BeforeEach
  void setUp() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    passwordGateway = new PasswordGatewayImpl(encoder);
  }

  @Test
  @DisplayName("Should generate a valid hash and match it with raw password")
  void shouldHashAndMatchPassword() {
    String rawPassword = "mySecretPassword123";

    String hash = passwordGateway.hash(rawPassword);

    assertNotNull(hash);
    assertNotEquals(rawPassword, hash);
    assertTrue(passwordGateway.matches(rawPassword, hash));
  }

  @Test
  @DisplayName("Should return false when passwords do not match")
  void shouldNotMatchWrongPassword() {
    String rawPassword = "correctPassword";
    String wrongPassword = "wrongPassword";
    String hash = passwordGateway.hash(rawPassword);

    assertFalse(passwordGateway.matches(wrongPassword, hash));
  }
}