package com.inktrack.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inktrack.core.exception.UnauthorizedException;
import com.inktrack.core.gateway.JwtGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtGatewayImpl implements JwtGateway {

  private final Algorithm algorithm;
  private final JWTVerifier verifier;

  public JwtGatewayImpl(@Value("${api.secret.key}") String secret) {
    this.algorithm = Algorithm.HMAC256(secret);

    this.verifier = JWT.require(algorithm)
        .withIssuer("Ink-auth-security")
        .build();
  }

  @Override
  public String generateAccessToken(UUID userId) {
    return generateToken(userId, getAccessTokenExpiry(), "access");
  }

  @Override
  public String generateRefreshToken(UUID userId) {
    return generateToken(userId, getRefreshTokenExpiry(), "refresh");
  }

  private String generateToken(UUID userId, Date expiresAt, String type) {
    return JWT.create()
        .withIssuer("Ink-auth-security")
        .withSubject(userId.toString())
        .withClaim("token_type", type)
        .withExpiresAt(expiresAt)
        .sign(algorithm);
  }

  @Override
  public UUID extractUserId(String token) {
    DecodedJWT decoded = verifier.verify(token);
    return UUID.fromString(decoded.getSubject());
  }

  @Override
  public String extractTokenType(String token) {
    DecodedJWT decoded = verifier.verify(token);
    return decoded.getClaim("token_type").asString();
  }

  @Override
  public UUID validateRefreshToken(String token) {
    DecodedJWT decoded = verifier.verify(token);

    String tokenType = decoded.getClaim("token_type").asString();
    if (tokenType == null || !tokenType.equals("refresh")) {
      throw new UnauthorizedException("Invalid token type: expected refresh token");
    }

    String userIdStr = decoded.getSubject();
    if (userIdStr == null) {
      throw new UnauthorizedException("User ID missing in refresh token");
    }

    return UUID.fromString(userIdStr);
  }
}
