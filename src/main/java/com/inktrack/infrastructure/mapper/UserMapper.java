package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.user.CreateUserRequestModel;
import com.inktrack.core.usecases.user.AuthRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.UserResponse;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public CreateUserRequestModel createRequestToRequestModel(CreateUserRequest request) {
    return new CreateUserRequestModel(request.name(), request.email(), request.password());
  }

  public UserEntity domainToEntity(User user) {
    return new UserEntity(user.getId(),user.getName(),user.getEmail(), user.getPassword(), user.getCreatedAt());
  }

  public User entityToDomain(UserEntity user){
    return new User(user.getId(), user.getName(), user.getEmail(), user.getPassword(),user.getCreatedAt());
  }

  public UserResponse userDomainToResponse(User user) {
    return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
  }

  public AuthRequest loginRequestToRequestModel(LoginRequest request) {
    return new AuthRequest(request.email(), request.password());
  }
}
