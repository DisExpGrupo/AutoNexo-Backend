package com.atg.autonexo.backend.iam.domain.model.entities;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing an email verification token.
 * Tokens expire after a certain period and can only be used once.
 */
@Entity
@Getter
@Setter
public class EmailVerificationToken extends AuditableModel {
    
    @Column(nullable = false, unique = true, length = 100)
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private com.atg.autonexo.backend.iam.domain.model.aggregates.User user;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    protected EmailVerificationToken() {}
    
    public EmailVerificationToken(String token, com.atg.autonexo.backend.iam.domain.model.aggregates.User user, LocalDateTime expiresAt) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }
    
    /**
     * Checks if the token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Checks if the token is valid (not expired and not used)
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
    
    /**
     * Marks the token as used
     */
    public void markAsUsed() {
        this.used = true;
    }
}


