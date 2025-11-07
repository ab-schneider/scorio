package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final String issuer;
    private final byte[] secret;
    private final long expiryMinutes;

    public JwtService(
            @Value("${scorio.jwt.issuer}") String issuer,
            @Value("${scorio.jwt.secret}") String secret,
            @Value("${scorio.jwt.expiryMinutes}") long expiryMinutes) {
        this.issuer = issuer;
        this.secret = secret.getBytes();
        this.expiryMinutes = expiryMinutes;
    }

    public String generate(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiryMinutes * 60)))
                .signWith(Keys.hmacShaKeyFor(secret), Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(Keys.hmacShaKeyFor(secret))
                .build()
                .parseSignedClaims(token);
    }
}