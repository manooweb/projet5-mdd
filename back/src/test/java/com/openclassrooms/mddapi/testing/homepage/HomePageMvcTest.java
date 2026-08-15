package com.openclassrooms.mddapi.testing.homepage;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.mddapi.system.config.SecurityConfig;
import com.openclassrooms.mddapi.system.controller.RootController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RootController.class)
@Import(SecurityConfig.class)
class HomePageMvcTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void getRootShouldDisplayTheTechnicalHomePage() throws Exception {
    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/html"))
        .andExpect(content().string(containsString("MDD API")))
        .andExpect(content().string(containsString("Technical entry point for the MDD backend.")))
        .andExpect(content().string(containsString("/swagger-ui/index.html")))
        .andExpect(content().string(containsString("http://localhost:8025")))
        .andExpect(content().string(containsString("/actuator/health")))
        .andExpect(content().string(containsString("/js/home-status.js")));
  }
}
