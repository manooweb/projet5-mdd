package com.openclassrooms.mddapi.integration;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.mddapi.authentication.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest(
    properties = {
      "mdd.authentication.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
      "mdd.authentication.jwt.secure-cookie=false"
    })
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthenticationCookieIT {

  private static final String AUTHENTICATION_COOKIE = "MDD_AUTH_TOKEN";
  private static final String JWT_SECRET = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtService jwtService;

  @Test
  void shouldCreateAnHttpOnlyJwtCookieWhenRegistering() throws Exception {
    String identifier = uniqueIdentifier();

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"username":"%s","email":"%s@example.test","password":"Pass1!wd"}
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
  void shouldReturnConflictWhenRegisteringWithAnExistingUsername() throws Exception {
    String existingIdentifier = uniqueIdentifier();
    String newIdentifier = uniqueIdentifier();
    register(existingIdentifier);

    registerRequest(existingIdentifier, newIdentifier + "@example.test")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Username or email is already used."))
        .andExpect(jsonPath("$.path").value("/api/auth/register"));
  }

  @Test
  void shouldReturnConflictWhenRegisteringWithAnExistingEmail() throws Exception {
    String existingIdentifier = uniqueIdentifier();
    String newIdentifier = uniqueIdentifier();
    register(existingIdentifier);

    registerRequest(newIdentifier, existingIdentifier + "@example.test")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Username or email is already used."))
        .andExpect(jsonPath("$.path").value("/api/auth/register"));
  }

  @Test
  void shouldRejectARegistrationPasswordThatDoesNotMeetThePasswordPolicy() throws Exception {
    String identifier = uniqueIdentifier();

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"username":"%s","email":"%s@example.test","password":"pass1!wd"}
                    """
                        .formatted(identifier, identifier))
                .with(csrfToken()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("The request contains an invalid value."))
        .andExpect(jsonPath("$.path").value("/api/auth/register"));
  }

  @Test
  void shouldCreateAnHttpOnlyJwtCookieWhenLoggingInWithUsername() throws Exception {
    String identifier = uniqueIdentifier();
    register(identifier);

    loginRequest(identifier, "Pass1!wd")
        .andExpect(status().isNoContent())
        .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
        .andExpect(cookie().value(AUTHENTICATION_COOKIE, not(emptyOrNullString())))
        .andExpect(cookie().httpOnly(AUTHENTICATION_COOKIE, true))
        .andExpect(cookie().secure(AUTHENTICATION_COOKIE, false))
        .andExpect(cookie().path(AUTHENTICATION_COOKIE, "/"))
        .andExpect(cookie().sameSite(AUTHENTICATION_COOKIE, "Lax"));
  }

  @Test
  void shouldCreateAnHttpOnlyJwtCookieWhenLoggingInWithEmail() throws Exception {
    String identifier = uniqueIdentifier();
    register(identifier);

    loginRequest(identifier + "@example.test", "Pass1!wd")
        .andExpect(status().isNoContent())
        .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
        .andExpect(cookie().value(AUTHENTICATION_COOKIE, not(emptyOrNullString())))
        .andExpect(cookie().httpOnly(AUTHENTICATION_COOKIE, true))
        .andExpect(cookie().secure(AUTHENTICATION_COOKIE, false))
        .andExpect(cookie().path(AUTHENTICATION_COOKIE, "/"))
        .andExpect(cookie().sameSite(AUTHENTICATION_COOKIE, "Lax"));
  }

  @Test
  void shouldRejectLoginWithAnInvalidPassword() throws Exception {
    String identifier = uniqueIdentifier();
    register(identifier);

    loginRequest(identifier, "incorrect password")
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message").value("Invalid credentials."))
        .andExpect(jsonPath("$.path").value("/api/auth/login"))
        .andExpect(cookie().doesNotExist(AUTHENTICATION_COOKIE));
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

  @Test
  void shouldReturnTheCurrentUserForAnAuthenticatedSession() throws Exception {
    String identifier = uniqueIdentifier();
    Cookie authenticationCookie = register(identifier);

    mockMvc
        .perform(get("/api/users/me").cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.username").value(identifier))
        .andExpect(jsonPath("$.email").value(identifier + "@example.test"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void shouldRejectTheCurrentUserRequestWhenTheAuthenticationCookieIsMissingOrExpired()
      throws Exception {
    mockMvc
        .perform(get("/api/users/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message").value("Authentication is required."))
        .andExpect(jsonPath("$.path").value("/api/users/me"));
  }

  @Test
  void shouldRejectTheCurrentUserRequestWhenTheJwtHasExpired() throws Exception {
    Cookie authenticationCookie = register(uniqueIdentifier());
    Long userId = jwtService.findUserId(authenticationCookie.getValue()).orElseThrow();
    SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    String expiredToken =
        Jwts.builder()
            .subject(userId.toString())
            .expiration(Date.from(Instant.now().minusSeconds(1)))
            .signWith(signingKey)
            .compact();

    mockMvc
        .perform(get("/api/users/me").cookie(new Cookie(AUTHENTICATION_COOKIE, expiredToken)))
        .andExpect(status().isUnauthorized());
  }

  private Cookie register(String identifier) throws Exception {
    MvcResult result =
        registerRequest(identifier, identifier + "@example.test")
            .andExpect(status().isCreated())
            .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
            .andReturn();

    return result.getResponse().getCookie(AUTHENTICATION_COOKIE);
  }

  private ResultActions registerRequest(String username, String email) throws Exception {
    return mockMvc.perform(
        post("/api/auth/register")
            .contentType(APPLICATION_JSON)
            .content(
                """
                {"username":"%s","email":"%s","password":"Pass1!wd"}
                """
                    .formatted(username, email))
            .with(csrfToken()));
  }

  private ResultActions loginRequest(String login, String password) throws Exception {
    return mockMvc.perform(
        post("/api/auth/login")
            .contentType(APPLICATION_JSON)
            .content(
                """
                {"login":"%s","password":"%s"}
                """
                    .formatted(login, password))
            .with(csrfToken()));
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
