package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.system.config.MddProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date; // NOSONAR: JJWT exposes Date for JWT temporal claims.
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final MddProperties properties;
  private final SecretKey signingKey;

  public JwtService(MddProperties properties) {
    this.properties = properties;
    signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getJwt().getSecret()));
  }

  public String createToken(UserAccount user) {
    Instant now = Instant.now();

    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("username", user.getUsername())
        .claim("sessionVersion", user.getSessionVersion())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(properties.getJwt().getExpiration())))
        .signWith(signingKey)
        .compact();
  }

  public Optional<Long> findUserId(String token) {
    return findAuthentication(token).map(AuthenticationToken::userId);
  }

  public Optional<AuthenticationToken> findAuthentication(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
      Number sessionVersion = claims.get("sessionVersion", Number.class);
      return Optional.of(
          new AuthenticationToken(
              Long.valueOf(claims.getSubject()),
              sessionVersion == null ? 0 : sessionVersion.longValue()));
    } catch (JwtException | IllegalArgumentException _) {
      return Optional.empty();
    }
  }

  public record AuthenticationToken(Long userId, long sessionVersion) {}
}
