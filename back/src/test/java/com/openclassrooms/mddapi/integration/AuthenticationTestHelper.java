package com.openclassrooms.mddapi.integration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

final class AuthenticationTestHelper {

  private static final String AUTHENTICATION_COOKIE = "MDD_AUTH_TOKEN";

  private final MockMvc mockMvc;

  AuthenticationTestHelper(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  Cookie register(String identifier) throws Exception {
    MvcResult result =
        registerRequest(identifier, identifier + "@example.test")
            .andExpect(status().isCreated())
            .andExpect(cookie().exists(AUTHENTICATION_COOKIE))
            .andReturn();

    return result.getResponse().getCookie(AUTHENTICATION_COOKIE);
  }

  ResultActions registerRequest(String username, String email) throws Exception {
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

  RequestPostProcessor csrfToken() throws Exception {
    MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf")).andReturn();
    Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
    return request -> {
      Cookie[] cookies = request.getCookies();
      if (cookies == null) {
        request.setCookies(csrfCookie);
      } else {
        Cookie[] cookiesWithCsrfToken = Arrays.copyOf(cookies, cookies.length + 1);
        cookiesWithCsrfToken[cookies.length] = csrfCookie;
        request.setCookies(cookiesWithCsrfToken);
      }
      request.addHeader("X-XSRF-TOKEN", csrfCookie.getValue());
      return request;
    };
  }

  String uniqueIdentifier() {
    return "user" + UUID.randomUUID().toString().replace("-", "");
  }
}
