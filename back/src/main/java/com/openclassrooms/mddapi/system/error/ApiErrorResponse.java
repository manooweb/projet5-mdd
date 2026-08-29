package com.openclassrooms.mddapi.system.error;

/**
 * Standard JSON representation of an API error.
 *
 * @param status HTTP status code
 * @param error HTTP reason phrase
 * @param messageCode stable application error code
 * @param message client-safe error message
 * @param path request path that produced the error
 */
public record ApiErrorResponse(
    int status, String error, String messageCode, String message, String path) {}
