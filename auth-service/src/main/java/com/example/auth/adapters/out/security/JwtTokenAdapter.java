package com.example.auth.adapters.out.security;

import com.example.auth.application.port.out.TokenPort;
import com.example.auth.application.dto.TokenValidationDTO;
import com.example.auth.domain.model.AuthenticatedUser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenAdapter implements TokenPort {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenAdapter(@Value("${security.jwt.secret}") String secret,
                           @Value("${security.jwt.expiration-seconds:30}") long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generate(AuthenticatedUser authenticatedUser) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(authenticatedUser.getUsername())
            .claim("role", authenticatedUser.getRole())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public TokenValidationDTO validate(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                return new TokenValidationDTO(false, null, null);
            }

            return new TokenValidationDTO(true, claims.getSubject(), claims.get("role", String.class));
        } catch (JwtException ex) {
            return new TokenValidationDTO(false, null, null);
        }
    }

    @Override
    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
