package com.inktrack.core.usecases.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserOutput(
    UUID id,
    String name,
    String email,
    LocalDateTime createdAt
) {
}
