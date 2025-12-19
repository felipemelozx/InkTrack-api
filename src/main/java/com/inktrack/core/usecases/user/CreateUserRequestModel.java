package com.inktrack.core.usecases.user;

public record CreateUserRequestModel(
    String name,
    String email,
    String passwordRaw
) {}