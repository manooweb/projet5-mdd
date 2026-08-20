package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.config.JwtProperties;
import com.openclassrooms.mddapi.authentication.domain.UserAccount;
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

  private final JwtProperties properties;
  private final SecretKey signingKey;

  public JwtService(JwtProperties properties) {
    this.properties = properties;
    signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
  }

  public String createToken(UserAccount user) {
    Instant now = Instant.now();

    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("username", user.getUsername())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(properties.expiration())))
        .signWith(signingKey)
        .compact();
  }

  public Optional<Long> findUserId(String token) {
    try {
      String subject =
          Jwts.parser()
              .verifyWith(signingKey)
              .build()
              .parseSignedClaims(token)
              .getPayload()
              .getSubject();
      return Optional.of(Long.valueOf(subject));
    } catch (JwtException | IllegalArgumentException _) {
      return Optional.empty();
    }
  }
}
