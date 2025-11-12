package com.atg.autonexo.backend.iam.interfaces.acl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ACL Facade for IAM operations exposed to other bounded contexts.
 * Specifically for Trust & Reputation context to update user trust scores.
 */
@Service
@RequiredArgsConstructor
public class IamFacade {
    
    private final UserRepository userRepository;
    
    /**
     * Update user trust score.
     * 
     * @param userId the user ID
     * @param trustScore the new trust score
     */
    public void updateUserTrustScore(Long userId, Float trustScore) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setTrustScore(trustScore);
            userRepository.save(user);
        });
    }
    
    /**
     * Get user trust score.
     * 
     * @param userId the user ID
     * @return the trust score if user exists
     */
    public Optional<Float> getUserTrustScore(Long userId) {
        return userRepository.findById(userId)
            .map(user -> user.getTrustScore());
    }
    
    /**
     * Check if user exists.
     * 
     * @param userId the user ID
     * @return true if user exists
     */
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
}

