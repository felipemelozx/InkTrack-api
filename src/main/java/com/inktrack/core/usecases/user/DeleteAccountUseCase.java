package com.inktrack.core.usecases.user;

import java.util.UUID;

public interface DeleteAccountUseCase {

  void execute(UUID userId, String currentPassword);
}