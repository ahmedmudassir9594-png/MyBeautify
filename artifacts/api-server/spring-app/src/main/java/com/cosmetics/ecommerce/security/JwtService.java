package com.cosmetics.ecommerce.security;

import com.cosmetics.ecommerce.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessTokenMinutes;
    private final long refreshTokenDays;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes,
                      @Value("${app.jwt.refresh-token-days}") long refreshTokenDays) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET or SESSION_SECRET must contain at least 32 characters.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutes = accessTokenMinutes;
        this.refreshTokenDays = refreshTokenDays;
    }

    public String accessToken(User user) {
        return build(user, accessTokenMinutes * 60);
    }

    public String refreshToken(User user) {
        return build(user, refreshTokenDays * 24 * 60 * 60);
    }

    private String build(User user, long seconds) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getUsername()).claim("role", user.getRole().name())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(seconds)))
                .signWith(key).compact();
    }

    public String subject(String token) {
        return parse(token).getPayload().getSubject();
    }

    public boolean isValid(String token, User user) {
        try {
            return user.getUsername().equals(subject(token)) && !parse(token).getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}