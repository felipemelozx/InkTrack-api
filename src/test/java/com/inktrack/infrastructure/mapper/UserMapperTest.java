package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.user.AuthRequest;
import com.inktrack.core.usecases.user.CreateUserRequestModel;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.UserResponse;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void createRequestToRequestModel_shouldMapCorrectly() {
        CreateUserRequest request = new CreateUserRequest("John", "john@email.com", "Password123!");
        CreateUserRequestModel model = userMapper.createRequestToRequestModel(request);

        assertNotNull(model);
        assertEquals(request.name(), model.name());
        assertEquals(request.email(), model.email());
        assertEquals(request.password(), model.passwordRaw());
    }

    @Test
    void domainToEntity_shouldMapCorrectly() {
        User user = new User(UUID.randomUUID(), "John", "john@email.com", "hashed_pass", LocalDateTime.now());
        UserEntity entity = userMapper.domainToEntity(user);

        assertNotNull(entity);
        assertEquals(user.getId(), entity.getId());
        assertEquals(user.getName(), entity.getName());
        assertEquals(user.getEmail(), entity.getEmail());
        assertEquals(user.getPassword(), entity.getPassword());
    }

    @Test
    void entityToDomain_shouldMapCorrectly() {
        UserEntity entity = new UserEntity(UUID.randomUUID(), "John", "john@email.com", "hashed_pass");
        entity.setCreatedAt(LocalDateTime.now());
        
        User user = userMapper.entityToDomain(entity);

        assertNotNull(user);
        assertEquals(entity.getId(), user.getId());
        assertEquals(entity.getName(), user.getName());
        assertEquals(entity.getEmail(), user.getEmail());
        assertEquals(entity.getPassword(), user.getPassword());
    }

    @Test
    void userDomainToResponse_shouldMapCorrectly() {
        User user = new User(UUID.randomUUID(), "John", "john@email.com", "hashed_pass", LocalDateTime.now());
        UserResponse response = userMapper.userDomainToResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getCreatedAt(), response.createdAt());
    }

    @Test
    void loginRequestToRequestModel_shouldMapCorrectly() {
        LoginRequest request = new LoginRequest("john@email.com", "Password123!");
        AuthRequest model = userMapper.loginRequestToRequestModel(request);

        assertNotNull(model);
        assertEquals(request.email(), model.email());
        assertEquals(request.password(), model.passwordRaw());
    }
}
