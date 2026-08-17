package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.EmailAlreadyExistsException;
import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.utils.Validation;

public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

  private final UserGateway userGateway;
  private final PasswordGateway passwordGateway;

  public UpdateUserUseCaseImpl(UserGateway userGateway, PasswordGateway passwordGateway) {
    this.userGateway = userGateway;
    this.passwordGateway = passwordGateway;
  }

  @Override
  public User execute(UpdateUserRequestModel requestModel) {
    User existing = userGateway.findById(requestModel.userId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", "User not found"));

    if (requestModel.name() != null && requestModel.name().isBlank()) {
      throw new FieldDomainValidationException("name", "Name is required");
    }

    boolean emailChanged = requestModel.email() != null
        && !requestModel.email().equals(existing.getEmail());

    if (emailChanged) {
      validateEmailChange(requestModel, existing);
    }

    String name = requestModel.name() != null ? requestModel.name() : existing.getName();
    String email = emailChanged ? requestModel.email() : existing.getEmail();

    User updated = new User(
        existing.getId(),
        name,
        email,
        existing.getPassword(),
        existing.getCreatedAt()
    );

    return userGateway.update(updated);
  }

  private void validateEmailChange(UpdateUserRequestModel requestModel, User existing) {
    if (!Validation.isValidEmail(requestModel.email())) {
      throw new FieldDomainValidationException("email", "Invalid email format");
    }

    if (requestModel.currentPassword() == null || requestModel.currentPassword().isBlank()) {
      throw new FieldDomainValidationException(
          "currentPassword",
          "Current password is required to change email"
      );
    }

    if (!passwordGateway.matches(requestModel.currentPassword(), existing.getPassword())) {
      throw new InvalidCredentialsException();
    }

    userGateway.findByEmail(requestModel.email())
        .filter(user -> !user.getId().equals(requestModel.userId()))
        .ifPresent(user -> {
          throw new EmailAlreadyExistsException(requestModel.email());
        });
  }
}