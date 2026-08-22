package com.openclassrooms.mddapi.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "mdd.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MddApiApplicationIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void contextLoads() {}

  @Test
  void shouldExposeMddInformationAndHomeLinkInOpenApiDocument() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.info.title").value("MDD API"))
        .andExpect(jsonPath("$.info.description").value(containsString("href=\"/\"")))
        .andExpect(jsonPath("$.info.description").value(containsString("🏠 API home")));
  }

  @Test
  void shouldExposePublicTechnicalEndpoints() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());

    mockMvc.perform(get("/actuator/health/db")).andExpect(status().isOk());

    mockMvc
        .perform(get("/swagger-ui/index.html"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/swagger-ui/swagger-ui/index.html"));

    mockMvc
        .perform(get("/swagger-ui/swagger-ui/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("/js/swagger-home-link.js")));

    mockMvc.perform(get("/")).andExpect(status().isOk());

    mockMvc
        .perform(get("/js/home-status.js"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("/actuator/health/db")));
  }
}
