package com.inktrack.core.gateway;

public interface PasswordGateway {
  String hash(String password);
  boolean matches(String passwordRaw, String passwordHash);
}
