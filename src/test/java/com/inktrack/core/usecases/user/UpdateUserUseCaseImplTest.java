package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.EmailAlreadyExistsException;
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
class UpdateUserUseCaseImplTest {

  @Mock
  private UserGateway userGateway;

  @Mock
  private PasswordGateway passwordGateway;

  private UpdateUserUseCaseImpl updateUserUseCase;

  private final UUID userId = UUID.randomUUID();

  private User existingUser;

  @BeforeEach
  void setUp() {
    updateUserUseCase = new UpdateUserUseCaseImpl(userGateway, passwordGateway);
    existingUser = new User(userId, "John Doe", "john@email.com", "hashed_password", LocalDateTime.now());
  }

  @Test
  void execute_shouldUpdateNameOnly_withoutCurrentPassword() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(userId, "Jane Doe", null, null);

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userGateway.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = updateUserUseCase.execute(request);

    assertEquals("Jane Doe", result.getName());
    assertEquals("john@email.com", result.getEmail());
    verify(userGateway, never()).findByEmail(any());
    verify(passwordGateway, never()).matches(any(), any());
  }

  @Test
  void execute_shouldUpdateNameAndKeepEmail_whenEmailNotProvided() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(userId, "Jane Doe", null, "wrong-password");

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userGateway.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = updateUserUseCase.execute(request);

    assertEquals("Jane Doe", result.getName());
    assertEquals("john@email.com", result.getEmail());
  }

  @Test
  void execute_shouldUpdateEmail_withCorrectCurrentPassword() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "new@email.com", "Password123!"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("Password123!", "hashed_password")).thenReturn(true);
    when(userGateway.findByEmail("new@email.com")).thenReturn(Optional.empty());
    when(userGateway.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = updateUserUseCase.execute(request);

    assertEquals("new@email.com", result.getEmail());
    assertEquals("John Doe", result.getName());
  }

  @Test
  void execute_shouldThrowException_whenEmailChangedWithoutPassword() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "new@email.com", null
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));

    assertThrows(FieldDomainValidationException.class, () -> updateUserUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenEmailChangedWithBlankPassword() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "new@email.com", "   "
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));

    assertThrows(FieldDomainValidationException.class, () -> updateUserUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenCurrentPasswordDoesNotMatch() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "new@email.com", "WrongPassword1!"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("WrongPassword1!", "hashed_password")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> updateUserUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenEmailAlreadyBelongsToAnotherUser() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "taken@email.com", "Password123!"
    );

    User otherUser = new User(UUID.randomUUID(), "Other", "taken@email.com", "hash", LocalDateTime.now());

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordGateway.matches("Password123!", "hashed_password")).thenReturn(true);
    when(userGateway.findByEmail("taken@email.com")).thenReturn(Optional.of(otherUser));

    assertThrows(EmailAlreadyExistsException.class, () -> updateUserUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldNotThrow_whenEmailIsSameAsCurrent() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "john@email.com", null
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userGateway.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = updateUserUseCase.execute(request);

    assertEquals("john@email.com", result.getEmail());
    verify(userGateway, never()).findByEmail(any());
  }

  @Test
  void execute_shouldThrowException_whenNameIsBlank() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(userId, "  ", null, null);

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));

    assertThrows(FieldDomainValidationException.class, () -> updateUserUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenEmailIsInvalid() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(
        userId, "John Doe", "invalid-email", "Password123!"
    );

    when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));

    assertThrows(FieldDomainValidationException.class, () -> updateUserUseCase.execute(request));
    verify(userGateway, never()).update(any());
  }

  @Test
  void execute_shouldThrowException_whenUserNotFound() {
    UpdateUserRequestModel request = new UpdateUserRequestModel(userId, "Jane Doe", null, null);

    when(userGateway.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> updateUserUseCase.execute(request));
  }
}