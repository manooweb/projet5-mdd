package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

@SpringBootTest(
    properties = {
      "mdd.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
      "mdd.jwt.secure-cookie=false"
    })
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PostCreationIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  private AuthenticationTestHelper authenticationTestHelper;

  @BeforeEach
  void setUp() {
    authenticationTestHelper = new AuthenticationTestHelper(mockMvc);
  }

  @Test
  void shouldCreateAPostForTheAuthenticatedUser() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long topicId = topicId("Java");

    mockMvc
        .perform(
            post("/api/posts")
                .cookie(authenticationCookie)
                .with(authenticationTestHelper.csrfToken())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"topicId":%d,"title":"Créer un article avec Spring","content":"Contenu de l'article."}
                    """
                        .formatted(topicId)))
        .andExpect(status().isCreated());

    assertThat(
            jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM posts
                WHERE topic_id = ?
                  AND user_id = (SELECT id FROM users WHERE username = ?)
                  AND title = ?
                  AND content = ?
                  AND created_at IS NOT NULL
                """,
                Integer.class,
                topicId,
                identifier,
                "Créer un article avec Spring",
                "Contenu de l'article."))
        .isOne();
  }

  @Test
  void shouldRejectPostCreationWhenTheTopicDoesNotExist() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);

    mockMvc
        .perform(
            post("/api/posts")
                .cookie(authenticationCookie)
                .with(authenticationTestHelper.csrfToken())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"topicId":999999,"title":"Titre valide","content":"Contenu valide."}
                    """))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.messageCode").value("RESOURCE_NOT_FOUND"));
  }

  private Long topicId(String topicName) {
    return jdbcTemplate.queryForObject(
        "SELECT id FROM topics WHERE name = ?", Long.class, topicName);
  }
}
