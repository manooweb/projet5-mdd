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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

/** Writes the JSON {@code 401 Unauthorized} response for unauthenticated API requests. */
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;
  private final MddProperties properties;

  public ApiAuthenticationEntryPoint(ObjectMapper objectMapper, MddProperties properties) {
    this.objectMapper = objectMapper;
    this.properties = properties;
  }

  @Override
  /**
   * Writes a client-safe authentication-required response.
   *
   * @param request rejected request
   * @param response HTTP response to populate
   * @param authenticationException Spring Security failure
   * @throws IOException when the JSON response cannot be written
   */
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authenticationException)
      throws IOException {
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(
        response.getOutputStream(),
        new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ApiErrorCode.AUTHENTICATION_REQUIRED.name(),
            properties.getMessages().getErrors().getAuthenticationRequired(),
            request.getRequestURI()));
  }
}
