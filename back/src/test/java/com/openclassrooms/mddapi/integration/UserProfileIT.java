package com.openclassrooms.mddapi.integration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(
    properties = {
      "mdd.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
      "mdd.jwt.secure-cookie=false"
    })
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserProfileIT {

  @Autowired private MockMvc mockMvc;

  private AuthenticationTestHelper authenticationTestHelper;

  @BeforeEach
  void setUp() {
    authenticationTestHelper = new AuthenticationTestHelper(mockMvc);
  }

  @Test
  void shouldUpdateTheAuthenticatedUserProfileWithoutChangingThePassword() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    String updatedUsername = identifier + "-updated";
    String updatedEmail = identifier + "-updated@example.test";

    mockMvc
        .perform(
            patch("/api/users/me")
                .cookie(authenticationCookie)
                .with(authenticationTestHelper.csrfToken())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"username":"%s","email":"%s","password":""}
                    """
                        .formatted(updatedUsername, updatedEmail)))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/api/users/me").cookie(authenticationCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(updatedUsername))
        .andExpect(jsonPath("$.email").value(updatedEmail));
  }

  @Test
  void shouldReplaceThePasswordWhenTheAuthenticatedUserProvidesANewOne() throws Exception {
    String identifier = authenticationTestHelper.uniqueIdentifier();
    Cookie authenticationCookie = authenticationTestHelper.register(identifier);
    String newPassword = "Updated1!";

    mockMvc
        .perform(
            patch("/api/users/me")
                .cookie(authenticationCookie)
                .with(authenticationTestHelper.csrfToken())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"username":"%s","email":"%s@example.test","password":"%s"}
                    """
                        .formatted(identifier, identifier, newPassword)))
        .andExpect(status().isNoContent());

    login(identifier, "Pass1!wd").andExpect(status().isUnauthorized());
    login(identifier, newPassword).andExpect(status().isNoContent());
  }

  private ResultActions login(String login, String password) throws Exception {
    return mockMvc.perform(
        post("/api/auth/login")
            .contentType(APPLICATION_JSON)
            .content("{\"login\":\"%s\",\"password\":\"%s\"}".formatted(login, password))
            .with(authenticationTestHelper.csrfToken()));
  }
}
