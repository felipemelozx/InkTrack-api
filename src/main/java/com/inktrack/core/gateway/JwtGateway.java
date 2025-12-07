package com.inktrack.core.gateway;

import java.util.Date;
import java.util.UUID;

public interface JwtGateway {

  String generateAccessToken(UUID userId);

  String generateRefreshToken(UUID userId);

  UUID extractUserId(String token);

  String extractTokenType(String token);

  UUID validateRefreshToken(String token);

  default Date getAccessTokenExpiry() {
    return new Date(
        java.time.LocalDateTime.now()
            .plusMinutes(15)
            .toInstant(java.time.ZoneOffset.UTC)
            .toEpochMilli()
    );
  }

  default Date getRefreshTokenExpiry() {
    return new Date(
        java.time.LocalDateTime.now()
            .plusDays(7)
            .toInstant(java.time.ZoneOffset.UTC)
            .toEpochMilli()
    );
  }
}
