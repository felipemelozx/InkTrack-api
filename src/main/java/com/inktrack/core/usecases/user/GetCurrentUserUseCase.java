package com.inktrack.core.usecases.user;

import java.util.UUID;

public interface GetCurrentUserUseCase {

  UserOutput execute(UUID userId);
}