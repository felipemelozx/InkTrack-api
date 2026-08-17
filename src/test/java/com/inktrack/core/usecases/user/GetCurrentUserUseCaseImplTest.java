package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserUseCaseImplTest {

  @Mock
  private UserGateway userGateway;

  private GetCurrentUserUseCaseImpl getCurrentUserUseCase;

  private final UUID userId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    getCurrentUserUseCase = new GetCurrentUserUseCaseImpl(userGateway);
  }

  @Test
  void execute_shouldReturnUserOutput_whenUserExists() {
    User user = new User(userId, "John Doe", "john@email.com", "hashed_password", LocalDateTime.now());

    when(userGateway.findById(userId)).thenReturn(Optional.of(user));

    UserOutput output = getCurrentUserUseCase.execute(userId);

    assertNotNull(output);
    assertEquals(userId, output.id());
    assertEquals("John Doe", output.name());
    assertEquals("john@email.com", output.email());
    assertEquals(user.getCreatedAt(), output.createdAt());
  }

  @Test
  void execute_shouldThrowException_whenUserNotFound() {
    when(userGateway.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> getCurrentUserUseCase.execute(userId));
  }
}