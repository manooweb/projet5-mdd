package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

  @Test
  void shouldListPostsFromNewestToOldest() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long authorId = userId(identifier);
    Long topicId = topicId("Java");
    Instant now = Instant.now();

    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Ancien article",
        "Contenu ancien.",
        authorId,
        topicId,
        now.minusSeconds(1));
    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Nouvel article",
        "Contenu récent.",
        authorId,
        topicId,
        now);

    mockMvc
        .perform(get("/api/posts").cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Nouvel article"))
        .andExpect(jsonPath("$[0].content").value("Contenu récent."))
        .andExpect(jsonPath("$[0].author").value(identifier))
        .andExpect(jsonPath("$[0].topic").value("Java"))
        .andExpect(jsonPath("$[0].createdAt").isString())
        .andExpect(jsonPath("$[1].title").value("Ancien article"));
  }

  private Long topicId(String topicName) {
    return jdbcTemplate.queryForObject(
        "SELECT id FROM topics WHERE name = ?", Long.class, topicName);
  }

  private Long userId(String username) {
    return jdbcTemplate.queryForObject(
        "SELECT id FROM users WHERE username = ?", Long.class, username);
  }
}
