package com.openclassrooms.mddapi.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/",
                        "/favicon.ico",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/webjars/**",
                        "/actuator/health/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .build();
  }
}
