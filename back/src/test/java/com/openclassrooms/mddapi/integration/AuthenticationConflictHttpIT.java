package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
      "mdd.authentication.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
      "mdd.authentication.jwt.secure-cookie=false"
    })
@Import(TestcontainersConfiguration.class)
class AuthenticationConflictHttpIT {

  @LocalServerPort private int port;

  private CookieManager cookieManager;
  private HttpClient httpClient;

  @BeforeEach
  void setUp() {
    cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    httpClient = HttpClient.newBuilder().cookieHandler(cookieManager).build();
  }

  @Test
  void shouldReturnConflictOverHttpWhenUsernameAlreadyExists() throws Exception {
    String existingIdentifier = uniqueIdentifier();
    String newIdentifier = uniqueIdentifier();
    String csrfToken = csrfToken();
    register(existingIdentifier, existingIdentifier + "@example.test", csrfToken, 201);

    register(existingIdentifier, newIdentifier + "@example.test", csrfToken, 409);
  }

  @Test
  void shouldReturnConflictOverHttpWhenEmailAlreadyExists() throws Exception {
    String existingIdentifier = uniqueIdentifier();
    String newIdentifier = uniqueIdentifier();
    String csrfToken = csrfToken();
    register(existingIdentifier, existingIdentifier + "@example.test", csrfToken, 201);

    register(newIdentifier, existingIdentifier + "@example.test", csrfToken, 409);
  }

  private void register(String username, String email, String csrfToken, int expectedStatus)
      throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder(uri("/api/auth/register"))
            .header("Content-Type", "application/json")
            .header("X-XSRF-TOKEN", csrfToken)
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {"username":"%s","email":"%s","password":"correct horse battery staple"}
                    """
                        .formatted(username, email)))
            .build();

    HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

    assertThat(response.statusCode()).isEqualTo(expectedStatus);
  }

  private String csrfToken() throws IOException, InterruptedException {
    HttpResponse<Void> response =
        httpClient.send(
            HttpRequest.newBuilder(uri("/api/auth/csrf")).GET().build(),
            HttpResponse.BodyHandlers.discarding());

    assertThat(response.statusCode()).isEqualTo(204);
    return cookieManager.getCookieStore().getCookies().stream()
        .filter(cookie -> cookie.getName().equals("XSRF-TOKEN"))
        .findFirst()
        .orElseThrow()
        .getValue();
  }

  private URI uri(String path) {
    return URI.create("http://localhost:%d%s".formatted(port, path));
  }

  private String uniqueIdentifier() {
    return "user" + UUID.randomUUID().toString().replace("-", "");
  }
}
