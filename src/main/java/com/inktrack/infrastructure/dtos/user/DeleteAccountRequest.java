package com.inktrack.infrastructure.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
    @NotBlank
    String currentPassword
) {
}