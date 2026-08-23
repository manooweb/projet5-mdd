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
  void shouldListPostsFromNewestToOldestByDefaultAndOldestToNewestWhenRequested() throws Exception {
    deletePostsAndComments();
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long authorId = userId(identifier);
    Long topicId = topicId("Java");
    subscribe(authorId, topicId);
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

    mockMvc
        .perform(get("/api/posts").param("sort", "asc").cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Ancien article"))
        .andExpect(jsonPath("$[1].title").value("Nouvel article"));
  }

  @Test
  void shouldOnlyListPostsFromTopicsFollowedByTheCurrentUser() throws Exception {
    deletePostsAndComments();
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long userId = userId(identifier);
    Long javaTopicId = topicId("Java");
    Long angularTopicId = topicId("Angular");
    subscribe(userId, javaTopicId);

    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Article Java suivi",
        "Visible dans le fil.",
        userId,
        javaTopicId,
        Instant.now());
    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Article Angular non suivi",
        "Absent du fil.",
        userId,
        angularTopicId,
        Instant.now());

    mockMvc
        .perform(get("/api/posts").cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
        .andExpect(jsonPath("$[0].title").value("Article Java suivi"))
        .andExpect(jsonPath("$[0].topic").value("Java"));
  }

  @Test
  void shouldGetAPostWithItsCommentsForTheAuthenticatedSubscriber() throws Exception {
    deletePostsAndComments();
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long authorId = userId(identifier);
    Long topicId = topicId("Java");
    subscribe(authorId, topicId);
    Instant createdAt = Instant.now();

    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Article détaillé",
        "Le contenu complet de l'article.",
        authorId,
        topicId,
        createdAt);
    Long postId =
        jdbcTemplate.queryForObject(
            "SELECT id FROM posts WHERE title = ?", Long.class, "Article détaillé");

    jdbcTemplate.update(
        """
        INSERT INTO comments (content, user_id, post_id, created_at)
        VALUES (?, ?, ?, ?)
        """,
        "Un commentaire utile.",
        authorId,
        postId,
        createdAt.plusSeconds(1));

    mockMvc
        .perform(get("/api/posts/{postId}", postId).cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(postId))
        .andExpect(jsonPath("$.title").value("Article détaillé"))
        .andExpect(jsonPath("$.content").value("Le contenu complet de l'article."))
        .andExpect(jsonPath("$.author").value(identifier))
        .andExpect(jsonPath("$.topic").value("Java"))
        .andExpect(jsonPath("$.createdAt").isString())
        .andExpect(jsonPath("$.comments[0].author").value(identifier))
        .andExpect(jsonPath("$.comments[0].content").value("Un commentaire utile."))
        .andExpect(jsonPath("$.comments[0].createdAt").isString());
  }

  @Test
  void shouldNotGetAPostFromATopicNotFollowedByTheAuthenticatedUser() throws Exception {
    deletePostsAndComments();
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long authorId = userId(identifier);
    Long javaTopicId = topicId("Java");
    Long angularTopicId = topicId("Angular");
    subscribe(authorId, javaTopicId);

    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Article Angular non suivi",
        "Ce contenu ne doit pas être accessible.",
        authorId,
        angularTopicId,
        Instant.now());
    Long postId =
        jdbcTemplate.queryForObject(
            "SELECT id FROM posts WHERE title = ?", Long.class, "Article Angular non suivi");

    mockMvc
        .perform(get("/api/posts/{postId}", postId).cookie(authenticationCookie))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.messageCode").value("RESOURCE_NOT_FOUND"));
  }

  @Test
  void shouldCreateACommentForTheAuthenticatedSubscriber() throws Exception {
    deletePostsAndComments();
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    Long authorId = userId(identifier);
    Long topicId = topicId("Java");
    subscribe(authorId, topicId);

    jdbcTemplate.update(
        """
        INSERT INTO posts (title, content, user_id, topic_id, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        "Article à commenter",
        "Son contenu.",
        authorId,
        topicId,
        Instant.now());
    Long postId =
        jdbcTemplate.queryForObject(
            "SELECT id FROM posts WHERE title = ?", Long.class, "Article à commenter");

    mockMvc
        .perform(
            post("/api/posts/{postId}/comments", postId)
                .cookie(authenticationCookie)
                .with(authenticationTestHelper.csrfToken())
                .contentType(APPLICATION_JSON)
                .content("{\"content\":\"Un commentaire utile.\"}"))
        .andExpect(status().isCreated());

    assertThat(
            jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM comments
                WHERE post_id = ?
                  AND user_id = ?
                  AND content = ?
                  AND created_at IS NOT NULL
                """,
                Integer.class,
                postId,
                authorId,
                "Un commentaire utile."))
        .isOne();
  }

  private void deletePostsAndComments() {
    jdbcTemplate.update("DELETE FROM comments");
    jdbcTemplate.update("DELETE FROM posts");
  }

  private Long topicId(String topicName) {
    return jdbcTemplate.queryForObject(
        "SELECT id FROM topics WHERE name = ?", Long.class, topicName);
  }

  private Long userId(String username) {
    return jdbcTemplate.queryForObject(
        "SELECT id FROM users WHERE username = ?", Long.class, username);
  }

  private void subscribe(Long userId, Long topicId) {
    jdbcTemplate.update(
        "INSERT INTO subscriptions (user_id, topic_id, created_at) VALUES (?, ?, ?)",
        userId,
        topicId,
        Instant.now());
  }
}
