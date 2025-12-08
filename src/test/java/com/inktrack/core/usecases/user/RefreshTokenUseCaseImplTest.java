package com.inktrack.core.usecases.user;

import com.inktrack.core.gateway.JwtGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.inktrack.core.exception.UnauthorizedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseImplTest {

    @Mock
    private JwtGateway jwtGateway;

    private RefreshTokenUseCaseImpl refreshTokenUseCase;

    @BeforeEach
    void setUp() {
        refreshTokenUseCase = new RefreshTokenUseCaseImpl(jwtGateway);
    }

    @Test
    void execute_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        String refreshToken = "valid_refresh_token";
        UUID userId = UUID.randomUUID();

        when(jwtGateway.validateRefreshToken(refreshToken)).thenReturn(userId);
        when(jwtGateway.generateAccessToken(userId)).thenReturn("new_access_token");

        AuthTokens tokens = refreshTokenUseCase.execute(refreshToken);

        assertNotNull(tokens);
        assertEquals("new_access_token", tokens.accessToken());
        assertEquals(refreshToken, tokens.refreshToken());
    }

    @Test
    void execute_shouldThrowException_whenRefreshTokenIsInvalid() {
        String refreshToken = "invalid_refresh_token";
        when(jwtGateway.validateRefreshToken(refreshToken)).thenThrow(new UnauthorizedException("Invalid token"));

        assertThrows(UnauthorizedException.class, () -> refreshTokenUseCase.execute(refreshToken));
    }
}
