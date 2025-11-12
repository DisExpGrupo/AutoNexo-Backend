package com.atg.autonexo.backend.iam.application.internal.outboundservices.notifications;

/**
 * Notification service interface for IAM context.
 * This service will be implemented by the Notifications bounded context.
 * For now, a mock implementation is provided for development purposes.
 */
public interface NotificationService {
    
    /**
     * Sends a password reset token to the user's email.
     * @param email the user's email address
     * @param token the password reset token
     */
    void sendPasswordResetToken(String email, String token);
    
    /**
     * Sends an email verification token to the user's email.
     * @param email the user's email address
     * @param token the email verification token
     */
    void sendEmailVerificationToken(String email, String token);
}


