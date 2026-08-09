package com.openclassrooms.mddapi.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

  @Bean
  OpenAPI mddOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("MDD API")
                .version("0.1.0")
                .description("REST API for MDD (Monde Du Dév)."));
  }
}
