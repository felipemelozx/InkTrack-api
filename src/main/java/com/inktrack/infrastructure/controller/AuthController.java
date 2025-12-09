package com.inktrack.infrastructure.controller;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.user.AuthRequest;
import com.inktrack.core.usecases.user.AuthTokens;
import com.inktrack.core.usecases.user.CreateUserRequestModel;
import com.inktrack.core.usecases.user.CreateUserUseCase;
import com.inktrack.core.usecases.user.LoginUseCase;
import com.inktrack.core.usecases.user.RefreshTokenUseCase;
import com.inktrack.infrastructure.dtos.user.CreateUserRequest;
import com.inktrack.infrastructure.dtos.user.UserResponse;
import com.inktrack.infrastructure.dtos.user.LoginRequest;
import com.inktrack.infrastructure.dtos.user.RefreshTokenRequest;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final CreateUserUseCase createUserUseCase;
  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final UserMapper userMapper;

  public AuthController(
      CreateUserUseCase createUserUseCase,
      LoginUseCase loginUseCase,
      RefreshTokenUseCase refreshTokenUseCase,
      UserMapper userMapper
  ) {
    this.createUserUseCase = createUserUseCase;
    this.loginUseCase = loginUseCase;
    this.refreshTokenUseCase = refreshTokenUseCase;
    this.userMapper = userMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
    CreateUserRequestModel requestModel = userMapper.createRequestToRequestModel(request);
    User userData = createUserUseCase.execute(requestModel);
    UserResponse userResponse = userMapper.userDomainToResponse(userData);
    ApiResponse<UserResponse> body = ApiResponse.success(userResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthTokens>> login(@Valid @RequestBody LoginRequest request) {
    AuthRequest requestModel = userMapper.loginRequestToRequestModel(request);
    AuthTokens tokens = loginUseCase.execute(requestModel);
    ApiResponse<AuthTokens> body = ApiResponse.success(tokens);
    return ResponseEntity.ok(body);
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthTokens>> refresh(
      @Valid @RequestBody RefreshTokenRequest request
  ) {
    AuthTokens tokens = refreshTokenUseCase.execute(request.refreshToken());
    ApiResponse<AuthTokens> body = ApiResponse.success(tokens);
    return ResponseEntity.ok(body);
  }
}
