package com.openclassrooms.mddapi.system.error;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final ApiErrorCode messageCode;

  public ApiException(HttpStatus status, ApiErrorCode messageCode, String message) {
    super(message);
    this.status = status;
    this.messageCode = messageCode;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public ApiErrorCode getMessageCode() {
    return messageCode;
  }
}
