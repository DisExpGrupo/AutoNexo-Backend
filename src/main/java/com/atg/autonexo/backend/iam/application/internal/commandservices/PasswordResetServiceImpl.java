package com.atg.autonexo.backend.iam.application.internal.commandservices;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.application.internal.outboundservices.hashing.HashingService;
import com.atg.autonexo.backend.iam.application.internal.outboundservices.notifications.NotificationService;
import com.atg.autonexo.backend.iam.domain.model.commands.RequestPasswordResetCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.ResetPasswordCommand;
import com.atg.autonexo.backend.iam.domain.model.entities.PasswordResetToken;
import com.atg.autonexo.backend.iam.domain.services.PasswordResetService;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.PasswordResetTokenRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

/**
 * Implementation of PasswordResetService.
 * Handles password reset token generation and password reset operations.
 */
@Service
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    private static final int TOKEN_EXPIRATION_HOURS = 24;
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final HashingService hashingService;
    private final NotificationService notificationService;
    
    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            HashingService hashingService,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.hashingService = hashingService;
        this.notificationService = notificationService;
    }
    
    @Override
    public void handle(RequestPasswordResetCommand command) {
        LOGGER.info("Processing password reset request for email: {}", command.email());
        
        // Find user by email
        Optional<com.atg.autonexo.backend.iam.domain.model.aggregates.User> userOptional = 
            userRepository.findByEmail(command.email());
        
        // For security reasons, don't reveal if user exists or not
        if (userOptional.isEmpty()) {
            LOGGER.warn("Password reset requested for non-existent email: {}", command.email());
            return; // Silently succeed to prevent email enumeration
        }
        
        com.atg.autonexo.backend.iam.domain.model.aggregates.User user = userOptional.get();
        
        // Check if user account is active
        if (!user.getActive()) {
            LOGGER.warn("Password reset requested for deactivated account: {}", command.email());
            return; // Silently succeed
        }
        
        // Invalidate any existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());
        
        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);
        
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiresAt);
        tokenRepository.save(resetToken);
        
        LOGGER.info("Password reset token generated for user: {}", user.getId());
        
        // Send notification (will be handled by Notifications BC)
        notificationService.sendPasswordResetToken(user.getEmail(), token);
    }
    
    @Override
    public void handle(ResetPasswordCommand command) {
        LOGGER.info("Processing password reset with token");
        
        // Find token
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(command.token());
        if (tokenOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }
        
        PasswordResetToken resetToken = tokenOptional.get();
        
        // Validate token
        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }
        
        // Get user
        com.atg.autonexo.backend.iam.domain.model.aggregates.User user = resetToken.getUser();
        
        // Hash new password
        String hashedPassword = hashingService.encode(command.newPassword());
        
        // Update user password
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
        
        // Mark token as used
        resetToken.markAsUsed();
        tokenRepository.save(resetToken);
        
        // Delete all tokens for this user (cleanup)
        tokenRepository.deleteByUserId(user.getId());
        
        LOGGER.info("Password reset successfully completed for user: {}", user.getId());
    }
}

