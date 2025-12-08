package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;

import java.util.Objects;

public final class LoginUseCaseImpl implements LoginUseCase{

  private final UserGateway userGateway;
  private final JwtGateway jwtGateway;
  private final PasswordGateway passwordGateway;

  public LoginUseCaseImpl(UserGateway userGateway, JwtGateway jwtGateway, PasswordGateway passwordGateway) {
    this.userGateway = userGateway;
    this.jwtGateway = jwtGateway;
    this.passwordGateway = passwordGateway;
  }

  @Override
  public AuthTokens execute(AuthRequest loginRequest) {
    Objects.requireNonNull(loginRequest.email(), "Email is Required");
    Objects.requireNonNull(loginRequest.passwordRaw(), "Password is Required");

    User user = userGateway.findByEmail(loginRequest.email())
        .orElseThrow(InvalidCredentialsException::new);

    boolean passwordMatches = passwordGateway.matches(loginRequest.passwordRaw(), user.getPassword());

    if(!passwordMatches) {
      throw new InvalidCredentialsException();
    }

    String accessToken = jwtGateway.generateAccessToken(user.getId());
    String refreshToken = jwtGateway.generateRefreshToken(user.getId());

    return new AuthTokens(accessToken, refreshToken);
  }

}
