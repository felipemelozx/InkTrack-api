package com.inktrack.infrastructure.controller;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.user.CreateUserRequestModel;
import com.inktrack.core.usecases.user.CreateUserUseCase;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.CreateUserResponse;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final CreateUserUseCase createUserUseCase;
  private final UserMapper userMapper;

  public AuthController(CreateUserUseCase createUserUseCase, UserMapper userMapper) {
    this.createUserUseCase = createUserUseCase;
    this.userMapper = userMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<CreateUserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
    CreateUserRequestModel requestModel = userMapper.createRequestToRequestModel(request);
    User userData = createUserUseCase.execute(requestModel);
    CreateUserResponse userResponse = userMapper.userDomainToCreateResponse(userData);
    ApiResponse<CreateUserResponse> body = ApiResponse.success(userResponse);
    return ResponseEntity.ok(body);
  }
}
