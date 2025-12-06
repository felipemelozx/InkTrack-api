package com.inktrack.core.usecases.user;

public record AuthRequest(String email, String passwordRaw) {
}
