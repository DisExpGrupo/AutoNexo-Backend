package com.atg.autonexo.backend.notifications.domain.services;

/**
 * Domain service interface for sending emails.
 * This service handles the core email sending functionality.
 */
public interface EmailService {
    
    /**
     * Sends a password reset email with the provided token.
     * @param to recipient email address
     * @param token password reset token
     * @param resetUrl optional reset URL (if null, will use default)
     */
    void sendPasswordResetEmail(String to, String token, String resetUrl);
    
    /**
     * Sends an email verification email with the provided token.
     * @param to recipient email address
     * @param token email verification token
     * @param verificationUrl optional verification URL (if null, will use default)
     */
    void sendEmailVerificationEmail(String to, String token, String verificationUrl);
    
    /**
     * Sends a workshop invitation email with the invitation code.
     * @param to recipient email address
     * @param invitationCode the invitation code
     * @param workshopName name of the workshop
     * @param invitationUrl optional invitation URL (if null, will use default)
     */
    void sendWorkshopInvitationEmail(String to, String invitationCode, String workshopName, String invitationUrl);
    
    /**
     * Sends a notification when an invitation expires.
     * @param to recipient email address
     * @param workshopName name of the workshop
     */
    void sendInvitationExpiredEmail(String to, String workshopName);
}

