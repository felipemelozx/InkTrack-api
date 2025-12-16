package com.inktrack.infrastructure.utils.response;

import com.inktrack.infrastructure.utils.CustomFieldError;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    List<CustomFieldError> errors,
    LocalDateTime timestamp) {

  public ApiResponse {
    errors = errors != null ? Collections.unmodifiableList(errors) : Collections.emptyList();
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, 
      "Operação realizada com sucesso.", 
      data, 
      Collections.emptyList(), 
      LocalDateTime.now()
    );
  }

  public static <T> ApiResponse<T> successNoContent() {
    return new ApiResponse<>(
      true, 
      "Operação realizada com sucesso.",
      null, Collections.emptyList(), 
      LocalDateTime.now()
    );
  }

  public static <T> ApiResponse<T> failure(List<CustomFieldError> errors, String message) {
    return new ApiResponse<>(false, message, null, errors, LocalDateTime.now());
  }

}