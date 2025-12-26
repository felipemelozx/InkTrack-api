package com.inktrack.infrastructure.gateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.inktrack.core.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtGatewayImplTest {

  private JwtGatewayImpl jwtGateway;
  private final String secret = "test-secret-key-123-test-secret-key-123";
  private final UUID userId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    jwtGateway = new JwtGatewayImpl(secret);
  }

  @Test
  @DisplayName("Should generate and extract user ID from Access Token")
  void shouldGenerateAndExtractFromAccessToken() {
    String token = jwtGateway.generateAccessToken(userId);

    assertNotNull(token);
    assertEquals(userId, jwtGateway.extractUserId(token));
    assertEquals("access", jwtGateway.extractTokenType(token));
  }

  @Test
  @DisplayName("Should generate and validate a valid Refresh Token")
  void shouldValidateCorrectRefreshToken() {
    String token = jwtGateway.generateRefreshToken(userId);

    UUID extractedId = jwtGateway.validateRefreshToken(token);

    assertEquals(userId, extractedId);
    assertEquals("refresh", jwtGateway.extractTokenType(token));
  }

  @Test
  @DisplayName("Should throw exception when validating Access Token as Refresh Token")
  void shouldThrowWhenValidatingAccessTokenAsRefresh() {
    String accessToken = jwtGateway.generateAccessToken(userId);

    assertThrows(UnauthorizedException.class, () -> {
      jwtGateway.validateRefreshToken(accessToken);
    });
  }

  @Test
  @DisplayName("Should throw exception for expired or invalid token signature")
  void shouldThrowForInvalidToken() {
    String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.payload";

    assertThrows(Exception.class, () -> {
      jwtGateway.extractUserId(invalidToken);
    });
  }

  @Test
  @DisplayName("Should throw UnauthorizedException when token_type claim is missing")
  void shouldThrowWhenTokenTypeClaimIsMissing() {
    // Gerando um token sem o claim "token_type"
    String tokenWithoutType = JWT.create()
        .withIssuer("Ink-auth-security")
        .withSubject(userId.toString())
        .sign(Algorithm.HMAC256(secret));

    UnauthorizedException exception = assertThrows(UnauthorizedException.class,
        () -> jwtGateway.validateRefreshToken(tokenWithoutType));

    assertEquals("Invalid token type: expected refresh token", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw UnauthorizedException when token_type is not 'refresh'")
  void shouldThrowWhenTokenTypeIsInvalid() {
    String tokenWithWrongType = JWT.create()
        .withIssuer("Ink-auth-security")
        .withSubject(userId.toString())
        .withClaim("token_type", "invalid_type")
        .sign(Algorithm.HMAC256(secret));

    UnauthorizedException exception = assertThrows(UnauthorizedException.class,
        () -> jwtGateway.validateRefreshToken(tokenWithWrongType));

    assertEquals("Invalid token type: expected refresh token", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw UnauthorizedException when subject (userId) is missing")
  void shouldThrowWhenSubjectIsMissing() {
    String tokenWithoutSubject = JWT.create()
        .withIssuer("Ink-auth-security")
        .withClaim("token_type", "refresh")
        .sign(Algorithm.HMAC256(secret));

    UnauthorizedException exception = assertThrows(UnauthorizedException.class,
        () -> jwtGateway.validateRefreshToken(tokenWithoutSubject));

    assertEquals("User ID missing in refresh token", exception.getMessage());
  }
}