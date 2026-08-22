package com.openclassrooms.mddapi.system.error;

public record ApiErrorResponse(
    int status, String error, String messageCode, String message, String path) {}
