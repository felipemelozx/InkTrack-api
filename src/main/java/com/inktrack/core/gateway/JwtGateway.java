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
    return Date.from(
        java.time.Instant.now().plusSeconds(15 * 60)  // 15 min
    );
  }


  default Date getRefreshTokenExpiry() {
    return Date.from(
        java.time.Instant.now().plusSeconds(7 * 24 * 60 * 60) // 7 dias
    );
  }
}
