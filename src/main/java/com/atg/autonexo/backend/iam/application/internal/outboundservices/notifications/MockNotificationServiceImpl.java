package com.atg.autonexo.backend.iam.application.internal.outboundservices.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of NotificationService for development purposes.
 * This will be replaced by the actual Notifications bounded context implementation.
 */
@Service
public class MockNotificationServiceImpl implements NotificationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MockNotificationServiceImpl.class);
    
    @Override
    public void sendPasswordResetToken(String email, String token) {
        LOGGER.info("=== MOCK EMAIL SERVICE ===");
        LOGGER.info("To: {}", email);
        LOGGER.info("Subject: Password Reset Request");
        LOGGER.info("Body: Please use the following token to reset your password: {}", token);
        LOGGER.info("Reset link: https://autonexo.app/reset-password?token={}", token);
        LOGGER.info("=========================");
    }
    
    @Override
    public void sendEmailVerificationToken(String email, String token) {
        LOGGER.info("=== MOCK EMAIL SERVICE ===");
        LOGGER.info("To: {}", email);
        LOGGER.info("Subject: Verify Your Email Address");
        LOGGER.info("Body: Please use the following token to verify your email: {}", token);
        LOGGER.info("Verification link: https://autonexo.app/verify-email?token={}", token);
        LOGGER.info("=========================");
    }
}


