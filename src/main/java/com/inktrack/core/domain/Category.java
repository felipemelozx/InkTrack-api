package com.inktrack.core.domain;

import java.time.OffsetDateTime;

public record Category(
    Long id,
    String name,
    OffsetDateTime createdAt
) {
}
