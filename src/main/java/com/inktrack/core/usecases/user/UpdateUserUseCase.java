package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;

public interface UpdateUserUseCase {

  User execute(UpdateUserRequestModel requestModel);
}