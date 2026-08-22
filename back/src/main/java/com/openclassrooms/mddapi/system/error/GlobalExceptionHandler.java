package com.openclassrooms.mddapi.system.error;

import com.openclassrooms.mddapi.system.config.MddProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MddProperties properties;

  public GlobalExceptionHandler(MddProperties properties) {
    this.properties = properties;
  }

  @ExceptionHandler(ApiException.class)
  ResponseEntity<ApiErrorResponse> handleApiException(
      ApiException exception, HttpServletRequest request) {
    return errorResponse(
        exception.getStatus(), exception.getMessageCode(), exception.getMessage(), request);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
  ResponseEntity<ApiErrorResponse> handleInvalidRequest(HttpServletRequest request) {
    return errorResponse(
        HttpStatus.BAD_REQUEST,
        ApiErrorCode.INVALID_REQUEST,
        properties.getMessages().getValidation().getInvalidRequest(),
        request);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  ResponseEntity<ApiErrorResponse> handleResourceNotFound(HttpServletRequest request) {
    return errorResponse(
        HttpStatus.NOT_FOUND,
        ApiErrorCode.RESOURCE_NOT_FOUND,
        properties.getMessages().getErrors().getResourceNotFound(),
        request);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiErrorResponse> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {
    log.error("Unexpected error while processing request {}", request.getRequestURI(), exception);
    return errorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ApiErrorCode.UNEXPECTED_ERROR,
        properties.getMessages().getErrors().getUnexpected(),
        request);
  }

  private ResponseEntity<ApiErrorResponse> errorResponse(
      HttpStatus status, ApiErrorCode messageCode, String message, HttpServletRequest request) {
    return ResponseEntity.status(status)
        .body(
            new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                messageCode.name(),
                message,
                request.getRequestURI()));
  }
}
