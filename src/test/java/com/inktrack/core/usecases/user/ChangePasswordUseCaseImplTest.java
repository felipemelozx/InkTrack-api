package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.FieldDomainValidationException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseImplTest {

  @Mock
  private UserGateway userGateway;

  @Mock
  private PasswordGateway passwordGateway;

  private ChangePasswordUseCaseImpl changePasswordUseCase;

  private final UUID userId = UUID.randomUUID();

  private User existingUser;

  @BeforeEach
  void setUp() {
    changePasswordUseCase = new ChangePasswordUseCaseImpl(userGateway, passwordGateway);
    existingUser = new User(userId, "John Doe", "john@email.com", "hashed_password", LocalDateTime.now());
  }

  @Test
  void execute_shouldChangePassword_whenCurrentPasswordMatches() {
    ChangePasswordRequestModel request = new ChangePasswordRequestModel(
        userId, "Password123!", "NewPassword123!"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("Password123!", "hashed_password")).thenReturn(true);
    when(passwordGateway.hash("NewPassword123!")).thenReturn("new_hashed_password");
    when(userGateway.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = changePasswordUseCase.execute(request);

    assertEquals("new_hashed_password", result.getPassword());
    assertEquals("john@email.com", result.getEmail());
  }

  @Test
  void execute_shouldThrowException_whenCurrentPasswordDoesNotMatch() {
    ChangePasswordRequestModel request = new ChangePasswordRequestModel(
        userId, "WrongPassword1!", "NewPassword123!"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("WrongPassword1!", "hashed_password")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> changePasswordUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenNewPasswordIsWeak() {
    ChangePasswordRequestModel request = new ChangePasswordRequestModel(
        userId, "Password123!", "weak"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("Password123!", "hashed_password")).thenReturn(true);

    assertThrows(FieldDomainValidationException.class, () -> changePasswordUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenUserNotFound() {
    ChangePasswordRequestModel request = new ChangePasswordRequestModel(
        userId, "Password123!", "NewPassword123!"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> changePasswordUseCase.execute(request));
  }
}