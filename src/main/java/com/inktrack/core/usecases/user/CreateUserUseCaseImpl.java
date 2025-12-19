package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.EmailAlreadyExistsException;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.utils.Validation;

import java.time.LocalDateTime;


public class CreateUserUseCaseImpl implements CreateUserUseCase {

  private final UserGateway userGateway;
  private final PasswordGateway passwordGateway;

  public CreateUserUseCaseImpl(UserGateway userGateway, PasswordGateway passwordGateway) {
    this.userGateway = userGateway;
    this.passwordGateway = passwordGateway;
  }

  @Override
  public User execute(CreateUserRequestModel requestModel) {
    Validation.validate(requestModel);
    userGateway.findByEmail(requestModel.email()).ifPresent(u -> {
      throw new EmailAlreadyExistsException(requestModel.email());
    });
    String passwordHash = passwordGateway.hash(requestModel.passwordRaw());
    User user = new User(null, requestModel.name(), requestModel.email(), passwordHash, LocalDateTime.now());
    return userGateway.save(user);
  }
}
