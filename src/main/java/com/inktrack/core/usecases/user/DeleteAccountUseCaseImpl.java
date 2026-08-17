package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;

import java.util.UUID;

public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {

  private final UserGateway userGateway;
  private final PasswordGateway passwordGateway;

  public DeleteAccountUseCaseImpl(UserGateway userGateway, PasswordGateway passwordGateway) {
    this.userGateway = userGateway;
    this.passwordGateway = passwordGateway;
  }

  @Override
  public void execute(UUID userId, String currentPassword) {
    User existing = userGateway.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", "User not found"));

    if (!passwordGateway.matches(currentPassword, existing.getPassword())) {
      throw new InvalidCredentialsException();
    }

    userGateway.deleteById(userId);
  }
}