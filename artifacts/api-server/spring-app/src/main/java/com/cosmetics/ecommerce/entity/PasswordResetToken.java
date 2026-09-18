package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens", indexes = @Index(name = "idx_reset_token", columnList = "token", unique = true))
public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 120) private String token;
    @OneToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @Column(nullable = false) private Instant expiresAt;
    protected PasswordResetToken() {}
    public PasswordResetToken(String token, User user, Instant expiresAt) { this.token = token; this.user = user; this.expiresAt = expiresAt; }
    public String getToken() { return token; } public User getUser() { return user; } public Instant getExpiresAt() { return expiresAt; }
    public boolean expired() { return Instant.now().isAfter(expiresAt); }
}