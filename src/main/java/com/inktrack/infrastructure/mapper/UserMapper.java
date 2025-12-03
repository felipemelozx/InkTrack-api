package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.user.CreateUserRequestModel;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public CreateUserRequestModel createRequestToRequestModel(CreateUserRequest request) {
    return new CreateUserRequestModel(request.name(), request.email(), request.password());
  }

  public UserEntity domainToEntity(User user) {
    return new UserEntity(user.getId(),user.getName(),user.getEmail(), user.getPassword());
  }

  public User entityToDomain(UserEntity user){
    return new User(user.getId(), user.getName(), user.getEmail(), user.getPassword(),user.getCreatedAt());
  }

  public CreateUserResponse userDomainToCreateResponse(User user) {
    return new CreateUserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
  }
}
