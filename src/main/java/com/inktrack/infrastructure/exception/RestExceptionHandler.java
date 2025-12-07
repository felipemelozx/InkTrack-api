package com.inktrack.infrastructure.exception;

import com.inktrack.core.exception.EmailAlreadyExistsException;
import com.inktrack.core.exception.EmailNotFoundException;
import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.exception.InvalidCredentialsException;
import com.inktrack.core.exception.UnauthorizedException;
import com.inktrack.infrastructure.utils.CustomFieldError;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> handleValidationExceptions(
      MethodArgumentNotValidException ex
  ) {
    List<CustomFieldError> errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof FieldError fieldError) {
            return new CustomFieldError(fieldError.getField(), fieldError.getDefaultMessage());
          } else {
            return new CustomFieldError("object", error.getDefaultMessage());
          }
        }).toList();

    return new ResponseEntity<>(
        ApiResponse.failure(errors, "Validation failed"),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> handlePathVariableValidationExceptions(
      HandlerMethodValidationException ex
  ) {

    List<CustomFieldError> errors = ex.getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof FieldError fieldError) {
            return new CustomFieldError(fieldError.getField(), fieldError.getDefaultMessage());
          } else {
            return new CustomFieldError("param", error.getDefaultMessage());
          }
        })
        .toList();

    return new ResponseEntity<>(
        ApiResponse.failure(errors, "Validation failed"),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(List.of(new CustomFieldError("email", ex.getMessage())), ex.getMessage()),
        HttpStatus.CONFLICT
    );
  }

  @ExceptionHandler(FieldDomainValidationException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleFieldDomainValidationException(
      FieldDomainValidationException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(
            List.of(new CustomFieldError(ex.getFieldName(), ex.getMessage())),
            "Domain validation failed for field: " + ex.getFieldName()
        ),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(EmailNotFoundException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleEmailNotFoundException(
      EmailNotFoundException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(List.of(new CustomFieldError("email", ex.getMessage())), ex.getMessage()),
        HttpStatus.BAD_REQUEST
    );
  }


  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleUnauthorizedException(
      UnauthorizedException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(List.of(new CustomFieldError("Credentials", ex.getMessage())), ex.getMessage()),
        HttpStatus.UNAUTHORIZED
    );
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleUnauthorizedException(
      InvalidCredentialsException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(List.of(new CustomFieldError("Credentials", ex.getMessage())), ex.getMessage()),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(com.auth0.jwt.exceptions.JWTDecodeException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleJWTDecodeException(
      com.auth0.jwt.exceptions.JWTDecodeException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(List.of(new CustomFieldError("token", "Invalid token format")), "Invalid token format"),
        HttpStatus.UNAUTHORIZED
    );
  }

  @ExceptionHandler(com.auth0.jwt.exceptions.JWTVerificationException.class)
  public ResponseEntity<ApiResponse<CustomFieldError>> handleJWTVerificationException(
      com.auth0.jwt.exceptions.JWTVerificationException ex
  ) {
    return new ResponseEntity<>(
        ApiResponse.failure(List.of(new CustomFieldError("token", "Invalid or expired token")), "Invalid or expired token"),
        HttpStatus.UNAUTHORIZED
    );
  }
}