package com.inktrack.core.gateway;

import java.util.Date;
import java.util.UUID;

public interface JwtGateway {

  long ACCESS_TOKEN_EXPIRY_SECONDS = 15L * 60;

  long REFRESH_TOKEN_EXPIRY_SECONDS = 7L * 24 * 60 * 60;

  String generateAccessToken(UUID userId);

  String generateRefreshToken(UUID userId);

  UUID extractUserId(String token);

  String extractTokenType(String token);

  UUID validateRefreshToken(String token);

  default Date getAccessTokenExpiry() {
    return Date.from(
        java.time.Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRY_SECONDS)
    );
  }


  default Date getRefreshTokenExpiry() {
    return Date.from(
        java.time.Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS)
    );
  }
}
