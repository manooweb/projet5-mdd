package com.openclassrooms.mddapi.system.security;

import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

/** Writes the JSON {@code 403 Forbidden} response for denied authenticated API requests. */
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;
  private final MddProperties properties;

  public ApiAccessDeniedHandler(ObjectMapper objectMapper, MddProperties properties) {
    this.objectMapper = objectMapper;
    this.properties = properties;
  }

  @Override
  /**
   * Writes a client-safe access-denied response.
   *
   * @param request denied request
   * @param response HTTP response to populate
   * @param accessDeniedException Spring Security denial
   * @throws IOException when the JSON response cannot be written
   */
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    writeErrorResponse(
        request,
        response,
        HttpStatus.FORBIDDEN,
        ApiErrorCode.ACCESS_DENIED,
        properties.getMessages().getErrors().getAccessDenied());
  }

  private void writeErrorResponse(
      HttpServletRequest request,
      HttpServletResponse response,
      HttpStatus status,
      ApiErrorCode messageCode,
      String message)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(
        response.getOutputStream(),
        new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            messageCode.name(),
            message,
            request.getRequestURI()));
  }
}
