package com.inktrack.infrastructure.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.inktrack.core.exception.EmailNotFoundException;
import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.persistence.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final JwtGateway jwtGateway;
  private final UserRepository userRepository;

  public SecurityFilter(JwtGateway jwtGateway, UserRepository userRepository) {
    this.jwtGateway = jwtGateway;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String token = recoverToken(request);

    if (token != null) {

      try {
        String tokenType = jwtGateway.extractTokenType(token);

        if (!"access".equals(tokenType)) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }

        UUID userId = jwtGateway.extractUserId(token);

        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new EmailNotFoundException("User Not Found"));

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var authentication = new UsernamePasswordAuthenticationToken(
            user, null, authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (TokenExpiredException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }

    return authHeader.substring(7);
  }
}