package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(
    properties = {
      "mdd.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
      "mdd.jwt.secure-cookie=false"
    })
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TopicSubscriptionIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  private AuthenticationTestHelper authenticationTestHelper;

  @BeforeEach
  void setUp() {
    authenticationTestHelper = new AuthenticationTestHelper(mockMvc);
  }

  @Test
  void shouldSubscribeTheAuthenticatedUserToATopic() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long javaTopicId = topicId("Java");

    mockMvc
        .perform(
            post("/api/topics/{topicId}/subscription", javaTopicId)
                .cookie(authenticationCookie)
                .with(authenticationTestHelper.csrfToken()))
        .andExpect(status().isNoContent());

    Integer subscriptions = subscriptionCount(identifier, javaTopicId);
    assertThat(subscriptions).isOne();
  }

  @Test
  void shouldKeepTheSubscriptionIdempotentWhenTheUserIsAlreadySubscribed() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long javaTopicId = topicId("Java");

    subscribe(authenticationCookie, javaTopicId).andExpect(status().isNoContent());
    subscribe(authenticationCookie, javaTopicId).andExpect(status().isNoContent());

    Integer subscriptions = subscriptionCount(identifier, javaTopicId);
    assertThat(subscriptions).isOne();
  }

  private Long topicId(String topicName) {
    return jdbcTemplate.queryForObject(
        "SELECT id FROM topics WHERE name = ?", Long.class, topicName);
  }

  private Integer subscriptionCount(String identifier, Long topicId) {
    return jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*)
        FROM subscriptions
        WHERE user_id = (SELECT id FROM users WHERE email = ?)
          AND topic_id = ?
        """,
        Integer.class,
        identifier + "@example.test",
        topicId);
  }

  private ResultActions subscribe(Cookie authenticationCookie, Long topicId) throws Exception {
    return mockMvc.perform(
        post("/api/topics/{topicId}/subscription", topicId)
            .cookie(authenticationCookie)
            .with(authenticationTestHelper.csrfToken()));
  }
}
