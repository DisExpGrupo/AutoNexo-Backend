package com.atg.autonexo.backend.iam.application.internal.commandservices;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.application.internal.outboundservices.notifications.NotificationService;
import com.atg.autonexo.backend.iam.domain.model.commands.ResendVerificationCommand;
import com.atg.autonexo.backend.iam.domain.model.commands.VerifyEmailCommand;
import com.atg.autonexo.backend.iam.domain.model.entities.EmailVerificationToken;
import com.atg.autonexo.backend.iam.domain.model.exceptions.UserNotFoundException;
import com.atg.autonexo.backend.iam.domain.services.EmailVerificationService;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.EmailVerificationTokenRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

/**
 * Implementation of EmailVerificationService.
 * Handles email verification token generation and verification operations.
 */
@Service
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);
    private static final int TOKEN_EXPIRATION_HOURS = 72; // 3 days
    
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    // Unused while email verification is disabled
    private final NotificationService notificationService;
    
    public EmailVerificationServiceImpl(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    public void handle(ResendVerificationCommand command) {
        LOGGER.info("Processing resend verification request for email: {}", command.email());
        
        // Find user by email
        Optional<com.atg.autonexo.backend.iam.domain.model.aggregates.User> userOptional = 
            userRepository.findByEmail(command.email());
        
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(command.email());
        }
        
        com.atg.autonexo.backend.iam.domain.model.aggregates.User user = userOptional.get();
        
        // Check if already verified
        if (user.isVerified()) {
            LOGGER.info("User {} is already verified", user.getId());
            return;
        }
        
        // Invalidate any existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());
        
        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);
        
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiresAt);
        tokenRepository.save(verificationToken);
        
        LOGGER.info("Email verification token generated for user: {}", user.getId());
        
        // Email verification is DISABLED to avoid synchronous SMTP bottlenecks.
        // This code remains commented until a proper email infrastructure is in place.
        // Send notification (will be handled by Notifications BC)
        // notificationService.sendEmailVerificationToken(user.getEmail(), token);
    }
    
    @Override
    public void handle(VerifyEmailCommand command) {
        LOGGER.info("Processing email verification with token");
        
        // Find token
        Optional<EmailVerificationToken> tokenOptional = tokenRepository.findByToken(command.token());
        if (tokenOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }
        
        EmailVerificationToken verificationToken = tokenOptional.get();
        
        // Validate token
        if (!verificationToken.isValid()) {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }
        
        // Get user
        com.atg.autonexo.backend.iam.domain.model.aggregates.User user = verificationToken.getUser();
        
        // Verify user email
        user.setVerified(true);
        userRepository.save(user);
        
        // Mark token as used
        verificationToken.markAsUsed();
        tokenRepository.save(verificationToken);
        
        // Delete all tokens for this user (cleanup)
        tokenRepository.deleteByUserId(user.getId());
        
        LOGGER.info("Email verified successfully for user: {}", user.getId());
    }
}


