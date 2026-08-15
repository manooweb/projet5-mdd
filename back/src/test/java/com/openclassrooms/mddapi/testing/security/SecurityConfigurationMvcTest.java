package com.openclassrooms.mddapi.testing.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.mddapi.system.config.SecurityConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = SecurityConfigurationMvcTest.CsrfProbeController.class)
@Import(SecurityConfig.class)
class SecurityConfigurationMvcTest {

  @Autowired private ApplicationContext applicationContext;

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldProvideABcryptPasswordEncoder() {
    assertThat(applicationContext.getBeansOfType(PasswordEncoder.class).values())
        .singleElement()
        .isInstanceOfSatisfying(
            BCryptPasswordEncoder.class,
            passwordEncoder ->
                assertThat(
                        passwordEncoder.matches(
                            "correct horse battery staple",
                            passwordEncoder.encode("correct horse battery staple")))
                    .isTrue());
  }

  @Test
  void shouldExposeACsrfTokenForBrowserClients() throws Exception {
    mockMvc
        .perform(get("/api/auth/csrf"))
        .andExpect(status().isNoContent())
        .andExpect(cookie().exists("XSRF-TOKEN"))
        .andExpect(cookie().httpOnly("XSRF-TOKEN", false));
  }

  @Test
  void shouldRejectAnAuthenticationWriteRequestWithoutACsrfToken() throws Exception {
    mockMvc.perform(post("/api/auth/csrf-probe")).andExpect(status().isForbidden());
  }

  @Test
  void shouldAcceptAnAuthenticationWriteRequestWithTheAngularCsrfCookieAndHeader()
      throws Exception {
    String csrfToken = "csrf-token";

    mockMvc
        .perform(
            post("/api/auth/csrf-probe")
                .cookie(new Cookie("XSRF-TOKEN", csrfToken))
                .header("X-XSRF-TOKEN", csrfToken))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldRequireAuthenticationForPrivateApiEndpoints() throws Exception {
    mockMvc.perform(get("/api/private-probe")).andExpect(status().isUnauthorized());
  }

  @RestController
  static class CsrfProbeController {

    @PostMapping("/api/auth/csrf-probe")
    ResponseEntity<Void> csrfProbe() {
      return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/private-probe")
    ResponseEntity<Void> privateProbe() {
      return ResponseEntity.noContent().build();
    }
  }
}
