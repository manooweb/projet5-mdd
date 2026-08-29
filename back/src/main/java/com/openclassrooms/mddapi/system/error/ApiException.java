package com.openclassrooms.mddapi.system.error;

import org.springframework.http.HttpStatus;

/** Runtime exception carrying the HTTP status and stable error code for an API failure. */
public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final ApiErrorCode messageCode;

  /**
   * Creates a client-safe API exception.
   *
   * @param status HTTP status returned to the client
   * @param messageCode stable application error code
   * @param message client-safe error message
   */
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
