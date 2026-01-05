package com.inktrack.core.usecases.category;

import java.time.OffsetDateTime;

public record CategoryOutput(
    Long id,
    String name,
    OffsetDateTime createdAt
) {
}
