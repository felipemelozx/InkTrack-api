package com.inktrack.infrastructure.dtos.user;

import com.inktrack.infrastructure.utils.anotations.ValidPassword;
import jakarta.validation.constraints.Email;

public record LoginRequest(
    @Email
    String email,
    @ValidPassword
    String password) {
}
