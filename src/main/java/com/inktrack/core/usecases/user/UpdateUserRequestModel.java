package com.inktrack.core.usecases.user;

import java.util.UUID;

public record UpdateUserRequestModel(
    UUID userId,
    String name,
    String email,
    String currentPassword
) {
}