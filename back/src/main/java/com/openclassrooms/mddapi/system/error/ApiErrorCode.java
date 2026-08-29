package com.openclassrooms.mddapi.system.error;

/** Stable error codes used by {@link ApiErrorResponse} and client-side error handling. */
public enum ApiErrorCode {
  DUPLICATE_IDENTITY,
  INVALID_CREDENTIALS,
  INVALID_REQUEST,
  RESOURCE_NOT_FOUND,
  AUTHENTICATION_REQUIRED,
  ACCESS_DENIED,
  UNEXPECTED_ERROR
}
