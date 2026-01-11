package com.ndiii.auth.security;

import com.ndiii.auth.domain.UserAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

  private final String secret;
  private final String issuer;
  private final long accessTokenMinutes;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.issuer}") String issuer,
      @Value("${app.jwt.accessTokenMinutes}") long accessTokenMinutes
  ) {
    this.secret = secret;
    this.issuer = issuer;
    this.accessTokenMinutes = accessTokenMinutes;
  }

  public String issueAccessToken(UserAccount user) {
    Instant now = Instant.now();
    Instant exp = now.plus(accessTokenMinutes, ChronoUnit.MINUTES);

    var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .issuer(issuer)
        .subject(user.getEmail())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of(
            "role", user.getRole().name()
        ))
        .signWith(key)
        .compact();
  }
}
