package com.openclassrooms.mddapi.integration;

import com.openclassrooms.mddapi.MddApiApplication;
import org.springframework.boot.SpringApplication;

public class TestMddApiApplication {

  public static void main(String[] args) {
    SpringApplication.from(MddApiApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
