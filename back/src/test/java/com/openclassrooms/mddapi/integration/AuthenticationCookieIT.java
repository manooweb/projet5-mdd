package com.openclassrooms.mddapi.integration;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthenticationCookieIT {

  private static final String AUTHENTICATION_COOKIE = "MDD_AUTH_TOKEN";

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldCreateAnHttpOnlyJwtCookieWhenRegistering() throws Exception {
    String identifier = uniqueIdentifier();

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"username":"%s","email":"%s@example.test","password":"correct horse battery staple"}
                    """
                        .formatted(identifier, identifier))
                .with(csrfToken()))
        .andExpect(status().isCreated())
        .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
        .andExpect(cookie().value(AUTHENTICATION_COOKIE, not(emptyOrNullString())))
        .andExpect(cookie().httpOnly(AUTHENTICATION_COOKIE, true))
        .andExpect(cookie().secure(AUTHENTICATION_COOKIE, false))
        .andExpect(cookie().path(AUTHENTICATION_COOKIE, "/"))
        .andExpect(cookie().sameSite(AUTHENTICATION_COOKIE, "Lax"));
  }

  @Test
  void shouldCreateAnHttpOnlyJwtCookieWhenLoggingIn() throws Exception {
    String identifier = uniqueIdentifier();
    register(identifier);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"login":"%s","password":"correct horse battery staple"}
                    """
                        .formatted(identifier))
                .with(csrfToken()))
        .andExpect(status().isNoContent())
        .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
        .andExpect(cookie().value(AUTHENTICATION_COOKIE, not(emptyOrNullString())))
        .andExpect(cookie().httpOnly(AUTHENTICATION_COOKIE, true))
        .andExpect(cookie().secure(AUTHENTICATION_COOKIE, false))
        .andExpect(cookie().path(AUTHENTICATION_COOKIE, "/"))
        .andExpect(cookie().sameSite(AUTHENTICATION_COOKIE, "Lax"));
  }

  @Test
  void shouldClearTheAuthenticationCookieWhenLoggingOut() throws Exception {
    String identifier = uniqueIdentifier();
    Cookie authenticationCookie = register(identifier);

    mockMvc
        .perform(post("/api/auth/logout").cookie(authenticationCookie).with(csrfToken()))
        .andExpect(status().isNoContent())
        .andExpect(cookie().maxAge(AUTHENTICATION_COOKIE, 0))
        .andExpect(cookie().httpOnly(AUTHENTICATION_COOKIE, true))
        .andExpect(cookie().path(AUTHENTICATION_COOKIE, "/"));
  }

  private Cookie register(String identifier) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {"username":"%s","email":"%s@example.test","password":"correct horse battery staple"}
                        """
                            .formatted(identifier, identifier))
                    .with(csrfToken()))
            .andExpect(status().isCreated())
            .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
            .andReturn();

    return result.getResponse().getCookie(AUTHENTICATION_COOKIE);
  }

  private RequestPostProcessor csrfToken() throws Exception {
    MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf")).andReturn();
    Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
    return request -> {
      request.setCookies(csrfCookie);
      request.addHeader("X-XSRF-TOKEN", csrfCookie.getValue());
      return request;
    };
  }

  private String uniqueIdentifier() {
    return "user" + UUID.randomUUID().toString().replace("-", "");
  }
}
