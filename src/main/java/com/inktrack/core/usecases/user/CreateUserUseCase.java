package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;

public interface CreateUserUseCase {
  User execute(CreateUserRequestModel requestModel);
}
