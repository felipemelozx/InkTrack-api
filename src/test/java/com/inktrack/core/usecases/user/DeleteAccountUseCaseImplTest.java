package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.exception.ResourceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseImplTest {

  @Mock
  private UserGateway userGateway;

  @Mock
  private PasswordGateway passwordGateway;

  private DeleteAccountUseCaseImpl deleteAccountUseCase;

  private final UUID userId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    deleteAccountUseCase = new DeleteAccountUseCaseImpl(userGateway, passwordGateway);
  }

  @Test
  void execute_shouldDeleteUser_whenCurrentPasswordMatches() {
    User existingUser = new User(userId, "John Doe", "john@email.com", "hashed_password", LocalDateTime.now());

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("Password123!", "hashed_password")).thenReturn(true);

    deleteAccountUseCase.execute(userId, "Password123!");

    verify(userGateway).deleteById(userId);
  }

  @Test
  void execute_shouldThrowException_whenCurrentPasswordDoesNotMatch() {
    User existingUser = new User(userId, "John Doe", "john@email.com", "hashed_password", LocalDateTime.now());

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("WrongPassword1!", "hashed_password")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> deleteAccountUseCase.execute(userId, "WrongPassword1!"));
    verify(userGateway, never()).deleteById(any());
  }

  @Test
  void execute_shouldThrowException_whenUserNotFound() {
    when(userGateway.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> deleteAccountUseCase.execute(userId, "Password123!"));
    verify(userGateway, never()).deleteById(any());
  }
}