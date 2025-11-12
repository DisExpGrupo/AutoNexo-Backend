package com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.entities.EmailVerificationToken;

/**
 * Repository for EmailVerificationToken entities.
 */
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    /**
     * Find an email verification token by token string.
     * @param token the token string
     * @return Optional containing the token if found
     */
    Optional<EmailVerificationToken> findByToken(String token);
    
    /**
     * Delete all tokens for a specific user (cleanup after verification).
     * @param user the user
     */
    void deleteByUser(User user);
    
    /**
     * Delete all tokens for a specific user ID (cleanup after verification).
     * @param userId the user ID
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}


