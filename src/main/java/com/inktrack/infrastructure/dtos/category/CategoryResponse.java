package com.inktrack.infrastructure.dtos.category;

import java.time.OffsetDateTime;

public record CategoryResponse(
    Long id,
    String name,
    OffsetDateTime createdAt
) {
}
