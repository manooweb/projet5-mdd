package com.openclassrooms.mddapi.integration;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.mddapi.authentication.service.JwtService;
import jakarta.servlet.http.Cookie;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
      "mdd.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
      "mdd.jwt.secure-cookie=false"
    })
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TopicsListIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtService jwtService;

  @Autowired private JdbcTemplate jdbcTemplate;

  private AuthenticationTestHelper authenticationTestHelper;

  @BeforeEach
  void setUp() {
    authenticationTestHelper = new AuthenticationTestHelper(mockMvc);
  }

  @Test
  void shouldReturnEverySeededTopicAsUnsubscribedForTheAuthenticatedUser() throws Exception {
    Cookie authenticationCookie =
        authenticationTestHelper.register(authenticationTestHelper.uniqueIdentifier());

    mockMvc
        .perform(get("/api/topics").cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[0].description").isString())
        .andExpect(jsonPath("$[?(@.name == 'Java')].subscribed", contains(false)))
        .andExpect(jsonPath("$[?(@.name == 'Spring Boot')].subscribed", contains(false)));
  }

  @Test
  void shouldReturnTheSubscriptionStatusForTheCurrentUserOnly() throws Exception {
    Cookie subscribedUserCookie =
        authenticationTestHelper.register(authenticationTestHelper.uniqueIdentifier());
    Cookie otherUserCookie =
        authenticationTestHelper.register(authenticationTestHelper.uniqueIdentifier());
    Long subscribedUserId = jwtService.findUserId(subscribedUserCookie.getValue()).orElseThrow();
    Integer javaTopicId =
        jdbcTemplate.queryForObject("SELECT id FROM topics WHERE name = ?", Integer.class, "Java");

    jdbcTemplate.update(
        "INSERT INTO subscriptions (user_id, topic_id, created_at) VALUES (?, ?, ?)",
        subscribedUserId,
        javaTopicId,
        Instant.now());

    mockMvc
        .perform(get("/api/topics").cookie(subscribedUserCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.name == 'Java')].subscribed", contains(true)))
        .andExpect(jsonPath("$[?(@.name == 'Spring Boot')].subscribed", contains(false)));

    mockMvc
        .perform(get("/api/topics").cookie(otherUserCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.name == 'Java')].subscribed", contains(false)));
  }

  @Test
  void shouldRejectTheTopicsRequestWithoutAnAuthenticatedSession() throws Exception {
    mockMvc
        .perform(get("/api/topics"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.messageCode").value("AUTHENTICATION_REQUIRED"));
  }
}
