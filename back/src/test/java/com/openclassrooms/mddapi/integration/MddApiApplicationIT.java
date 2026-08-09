package com.openclassrooms.mddapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MddApiApplicationIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void contextLoads() {}

  @Test
  void shouldExposePublicTechnicalEndpoints() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());

    mockMvc.perform(get("/actuator/health/db")).andExpect(status().isOk());

    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());

    mockMvc
        .perform(get("/swagger-ui/index.html"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/swagger-ui/swagger-ui/index.html"));

    mockMvc.perform(get("/swagger-ui/swagger-ui/index.html")).andExpect(status().isOk());
  }
}
