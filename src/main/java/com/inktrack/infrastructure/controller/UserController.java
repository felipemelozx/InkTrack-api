package com.inktrack.infrastructure.controller;

import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.user.ChangePasswordRequestModel;
import com.inktrack.core.usecases.user.ChangePasswordUseCase;
import com.inktrack.core.usecases.user.DeleteAccountUseCase;
import com.inktrack.core.usecases.user.GetCurrentUserUseCase;
import com.inktrack.core.usecases.user.UpdateUserRequestModel;
import com.inktrack.core.usecases.user.UpdateUserUseCase;
import com.inktrack.core.usecases.user.UserOutput;
import com.inktrack.infrastructure.dtos.user.ChangePasswordRequest;
import com.inktrack.infrastructure.dtos.user.DeleteAccountRequest;
import com.inktrack.infrastructure.dtos.user.UpdateUserRequest;
import com.inktrack.infrastructure.dtos.user.UserResponse;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final GetCurrentUserUseCase getCurrentUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final ChangePasswordUseCase changePasswordUseCase;
  private final DeleteAccountUseCase deleteAccountUseCase;
  private final UserMapper userMapper;

  public UserController(
      GetCurrentUserUseCase getCurrentUserUseCase,
      UpdateUserUseCase updateUserUseCase,
      ChangePasswordUseCase changePasswordUseCase,
      DeleteAccountUseCase deleteAccountUseCase,
      UserMapper userMapper
  ) {
    this.getCurrentUserUseCase = getCurrentUserUseCase;
    this.updateUserUseCase = updateUserUseCase;
    this.changePasswordUseCase = changePasswordUseCase;
    this.deleteAccountUseCase = deleteAccountUseCase;
    this.userMapper = userMapper;
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    UserOutput userOutput = getCurrentUserUseCase.execute(currentUser.getId());
    UserResponse response = userMapper.userOutputToResponse(userOutput);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @Valid @RequestBody UpdateUserRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    UpdateUserRequestModel requestModel = userMapper.updateRequestToRequestModel(
        currentUser.getId(),
        request
    );
    User updatedUser = updateUserUseCase.execute(requestModel);
    UserResponse response = userMapper.userDomainToResponse(updatedUser);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/me/password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    ChangePasswordRequestModel requestModel = userMapper.changePasswordRequestToRequestModel(
        currentUser.getId(),
        request
    );
    changePasswordUseCase.execute(requestModel);
    return ResponseEntity.ok(ApiResponse.successNoContent());
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteAccount(
      @Valid @RequestBody DeleteAccountRequest request,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    deleteAccountUseCase.execute(currentUser.getId(), request.currentPassword());
    return ResponseEntity.noContent().build();
  }
}