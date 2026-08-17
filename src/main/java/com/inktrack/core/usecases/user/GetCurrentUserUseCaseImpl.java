package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.UserGateway;

import java.util.UUID;

public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {

  private final UserGateway userGateway;

  public GetCurrentUserUseCaseImpl(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public UserOutput execute(UUID userId) {
    User user = userGateway.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", "User not found"));
    return new UserOutput(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
  }
}