package com.mipt.todo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

  private final SecretKey signingKey;
  private final long expirationSeconds;

  public JwtUtils(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationSeconds = expirationSeconds;
  }

  public String generateToken(UserDetails userDetails) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expirationSeconds);

    String authorities = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("authorities", authorities)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(signingKey)
        .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      Date expiration = parseClaims(token).getExpiration();
      return expiration != null && expiration.after(new Date());
    } catch (Exception ex) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}

