package com.inktrack.core.usecases.user;

import com.inktrack.core.domain.User;
import com.inktrack.core.exception.EmailAlreadyExistsException;
import com.inktrack.core.exception.FieldDomainValidationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordGateway passwordGateway;

    private CreateUserUseCaseImpl createUserUseCase;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCaseImpl(userGateway, passwordGateway);
    }

    @Test
    void execute_shouldCreateUser_whenDataIsValid() {
        CreateUserRequestModel request = new CreateUserRequestModel("John Doe", "john@email.com", "Password123!");
        
        when(userGateway.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordGateway.hash(request.passwordRaw())).thenReturn("hashed_password");
        when(userGateway.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new User(UUID.randomUUID(), u.getName(), u.getEmail(), u.getPassword(), LocalDateTime.now());
        });

        User result = createUserUseCase.execute(request);

        assertNotNull(result);
        assertEquals(request.name(), result.getName());
        assertEquals(request.email(), result.getEmail());
        assertNotNull(result.getId());
        verify(userGateway).save(any(User.class));
    }

    @Test
    void execute_shouldThrowException_whenEmailAlreadyExists() {
        CreateUserRequestModel request = new CreateUserRequestModel("John Doe", "john@email.com", "Password123!");
        
        when(userGateway.findByEmail(request.email())).thenReturn(Optional.of(new User(UUID.randomUUID(), "Existing", "john@email.com", "pass", LocalDateTime.now())));

        assertThrows(EmailAlreadyExistsException.class, () -> createUserUseCase.execute(request));
        verify(userGateway, never()).save(any());
    }

    @Test
    void execute_shouldThrowValidationException_whenInputIsInvalid() {
        CreateUserRequestModel request = new CreateUserRequestModel("", "invalid", "weak");
        
        assertThrows(FieldDomainValidationException.class, () -> createUserUseCase.execute(request));
        verify(userGateway, never()).findByEmail(any());
        verify(userGateway, never()).save(any());
    }
}
