package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;

public interface ChangePasswordUseCase {

  User execute(ChangePasswordRequestModel requestModel);
}