package com.inktrack.infrastructure.dtos.user;

import com.inktrack.infrastructure.utils.anotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
    @NotBlank
    String name,
    @Email
    String email,
    @ValidPassword
    String password
) {
}
