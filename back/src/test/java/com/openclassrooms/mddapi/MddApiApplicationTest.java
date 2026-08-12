package com.openclassrooms.mddapi;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class MddApiApplicationTest {

  @Test
  void shouldRunApplication() {
    String[] args = {};

    try (MockedStatic<SpringApplication> mockedSpringApplication =
        mockStatic(SpringApplication.class)) {
      MddApiApplication.main(args);

      mockedSpringApplication.verify(() -> SpringApplication.run(MddApiApplication.class, args));
    }
  }
}
