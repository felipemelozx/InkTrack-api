package com.inktrack.infrastructure.dtos.user;

import com.inktrack.infrastructure.utils.anotations.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank
    String currentPassword,
    @ValidPassword
    String newPassword
) {
}