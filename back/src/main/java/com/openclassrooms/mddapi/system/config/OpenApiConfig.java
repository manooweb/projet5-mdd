package com.openclassrooms.mddapi.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
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
                .description(
                    """
                    REST API for MDD (Monde Du Dév).

                    <a href="/" target="_self">🏠 API home</a>
                    """))
        .path(
            "/api/auth/csrf",
            new PathItem()
                .get(
                    new Operation()
                        .summary("Initialize CSRF protection")
                        .description(
                            "Call this endpoint once before sending requests that modify data.")
                        .responses(
                            new ApiResponses()
                                .addApiResponse(
                                    "204",
                                    new ApiResponse()
                                        .description("CSRF protection initialized.")))));
  }
}
