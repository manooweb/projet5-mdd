package com.openclassrooms.mddapi.authentication.security;

import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.authentication.service.AuthenticationCookieService;
import com.openclassrooms.mddapi.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserAccountRepository userAccountRepository;

  public JwtAuthenticationFilter(
      JwtService jwtService, UserAccountRepository userAccountRepository) {
    this.jwtService = jwtService;
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      authenticationCookie(request)
          .flatMap(jwtService::findUserId)
          .flatMap(userAccountRepository::findById)
          .ifPresent(user -> authenticate(user.getId(), request));
    }

    filterChain.doFilter(request, response);
  }

  private Optional<String> authenticationCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> AuthenticationCookieService.COOKIE_NAME.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();
  }

  private void authenticate(Long userId, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userId.toString(), null, AuthorityUtils.NO_AUTHORITIES);
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
