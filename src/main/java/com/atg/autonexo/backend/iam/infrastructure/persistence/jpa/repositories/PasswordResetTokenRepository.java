package com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.entities.PasswordResetToken;

/**
 * Repository for PasswordResetToken entities.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find a password reset token by token string.
     * @param token the token string
     * @return Optional containing the token if found
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Delete all tokens for a specific user (cleanup after password reset).
     * @param user the user
     */
    void deleteByUser(User user);
    
    /**
     * Delete all tokens for a specific user ID (cleanup after password reset).
     * @param userId the user ID
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

