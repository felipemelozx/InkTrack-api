package com.inktrack.core.usecases.user;

public interface LoginUseCase {
  AuthTokens execute(AuthRequest loginRequest);
}
