package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.utils.Validation;

public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

  private final UserGateway userGateway;
  private final PasswordGateway passwordGateway;

  public ChangePasswordUseCaseImpl(UserGateway userGateway, PasswordGateway passwordGateway) {
    this.userGateway = userGateway;
    this.passwordGateway = passwordGateway;
  }

  @Override
  public User execute(ChangePasswordRequestModel requestModel) {
    User existing = userGateway.findById(requestModel.userId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", "User not found"));

    if (!passwordGateway.matches(requestModel.currentPassword(), existing.getPassword())) {
      throw new InvalidCredentialsException();
    }

    if (!Validation.isStrongPassword(requestModel.newPassword())) {
      throw new FieldDomainValidationException("password", "Password must be stronger");
    }

    String passwordHash = passwordGateway.hash(requestModel.newPassword());

    User updated = new User(
        existing.getId(),
        existing.getName(),
        existing.getEmail(),
        passwordHash,
        existing.getCreatedAt()
    );

    return userGateway.update(updated);
  }
}