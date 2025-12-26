package com.inktrack.infrastructure.gateway;

import com.inktrack.core.gateway.PasswordGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordGatewayImpl implements PasswordGateway {

  private final BCryptPasswordEncoder encoder;

  public PasswordGatewayImpl(BCryptPasswordEncoder encoder) {
    this.encoder = encoder;
  }

  @Override
  public String hash(String password) {
    return encoder.encode(password);
  }

  @Override
  public boolean matches(String passwordRaw, String passwordHash) {
    return encoder.matches(passwordRaw, passwordHash);
  }
}
