package com.inktrack.core.usecases.user;

import java.util.UUID;

public record ChangePasswordRequestModel(
    UUID userId,
    String currentPassword,
    String newPassword
) {
}