package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private JwtGateway jwtGateway;

    @Mock
    private PasswordGateway passwordGateway;

    private LoginUseCaseImpl loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCaseImpl(userGateway, jwtGateway, passwordGateway);
    }

    @Test
    void execute_shouldReturnAuthTokens_whenCredentialsAreValid() {
        AuthRequest request = new AuthRequest("john@email.com", "StrongP@ss1");
        User user = new User(UUID.randomUUID(), "John", "john@email.com", "hashed_password", LocalDateTime.now());

        when(userGateway.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordGateway.matches(request.passwordRaw(), user.getPassword())).thenReturn(true);
        when(jwtGateway.generateAccessToken(user.getId())).thenReturn("access_token");
        when(jwtGateway.generateRefreshToken(user.getId())).thenReturn("refresh_token");

        AuthTokens tokens = loginUseCase.execute(request);

        assertNotNull(tokens);
        assertEquals("access_token", tokens.accessToken());
        assertEquals("refresh_token", tokens.refreshToken());
    }

    @Test
    void execute_shouldThrowException_whenUserNotFound() {
        AuthRequest request = new AuthRequest("john@email.com", "StrongP@ss1");
        when(userGateway.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));
    }

    @Test
    void execute_shouldThrowException_whenPasswordDoesNotMatch() {
        AuthRequest request = new AuthRequest("john@email.com", "WrongP@ss1");
        User user = new User(UUID.randomUUID(), "John", "john@email.com", "hashed_password", LocalDateTime.now());

        when(userGateway.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordGateway.matches(request.passwordRaw(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));
    }
}
