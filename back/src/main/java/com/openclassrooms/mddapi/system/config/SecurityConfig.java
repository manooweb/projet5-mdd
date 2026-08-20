package com.openclassrooms.mddapi.system.config;

import com.openclassrooms.mddapi.authentication.security.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, ObjectProvider<JwtAuthenticationFilter> jwtAuthenticationFilterProvider) {
    // The CSRF token must be readable by the SPA to populate the X-XSRF-TOKEN header.
    // It is not an authentication credential; the JWT cookie remains HttpOnly.
    CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/",
                        "/favicon.ico",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/webjars/**",
                        "/api/auth/**",
                        "/actuator/health/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()
                    .dispatcherTypeMatchers(DispatcherType.ERROR)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .csrf(
            csrf ->
                csrf.csrfTokenRepository(csrfTokenRepository)
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
        .addFilterBefore(new CsrfTokenEndpointFilter(csrfTokenRepository), CsrfFilter.class);

    jwtAuthenticationFilterProvider.ifAvailable(
        jwtAuthenticationFilter ->
            http.addFilterBefore(
                jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class));

    return http.httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .build();
  }

  private static class CsrfTokenEndpointFilter extends OncePerRequestFilter {

    private final CookieCsrfTokenRepository csrfTokenRepository;

    CsrfTokenEndpointFilter(CookieCsrfTokenRepository csrfTokenRepository) {
      this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
      return !"GET".equals(request.getMethod())
          || !"/api/auth/csrf".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
      csrfTokenRepository.saveToken(csrfToken, request, response);
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
  }
}
