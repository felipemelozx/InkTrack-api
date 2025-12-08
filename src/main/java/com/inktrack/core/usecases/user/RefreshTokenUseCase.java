package com.inktrack.core.usecases.user;

public interface RefreshTokenUseCase {

  AuthTokens execute(String refreshToken);

}
