package com.inktrack.core.usecases.user;

import com.inktrack.core.gateway.JwtGateway;

import java.util.UUID;

public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase{

  private final JwtGateway jwtGateway;

  public RefreshTokenUseCaseImpl(JwtGateway jwtGateway) {
    this.jwtGateway = jwtGateway;
  }

  @Override
  public AuthTokens execute(String refreshToken) {
    UUID userId = jwtGateway.validateRefreshToken(refreshToken);
    String newAccessToken = jwtGateway.generateAccessToken(userId);
    return new AuthTokens(newAccessToken, refreshToken);
  }
}
