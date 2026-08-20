package com.openclassrooms.mddapi.authentication.config;

import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.authentication.security.JwtAuthenticationFilter;
import com.openclassrooms.mddapi.authentication.service.JwtService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({JwtProperties.class, AuthenticationMessagesProperties.class})
public class AuthenticationConfiguration {

  @Bean
  JwtAuthenticationFilter jwtAuthenticationFilter(
      JwtService jwtService, UserAccountRepository userAccountRepository) {
    return new JwtAuthenticationFilter(jwtService, userAccountRepository);
  }
}
