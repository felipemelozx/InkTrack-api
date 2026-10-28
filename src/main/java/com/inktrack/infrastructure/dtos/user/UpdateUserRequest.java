package com.inktrack.infrastructure.dtos.user;

public record UpdateUserRequest(
    String name,
    String email,
    String currentPassword
) {
}