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
    
    /**
     * Sends a maintenance reminder email.
     * @param to recipient email address
     * @param vehicleBrand vehicle brand
     * @param vehicleModel vehicle model
     * @param vehicleYear vehicle year
     * @param reminderType type of reminder ("mileage" or "time")
     * @param details additional details about the reminder
     */
    void sendMaintenanceReminderEmail(String to, String vehicleBrand, String vehicleModel, 
                                     Integer vehicleYear, String reminderType, String details);
    
    /**
     * Sends a notification when a new offer is received for a service request.
     * @param to recipient email address (user)
     * @param serviceRequestId the service request ID
     * @param offerId the offer ID
     * @param workshopName name of the workshop that sent the offer
     */
    void sendNewOfferEmail(String to, Long serviceRequestId, Long offerId, String workshopName);
    
    /**
     * Sends a notification when an offer is accepted.
     * @param to recipient email address (workshop)
     * @param offerId the offer ID
     * @param userName name of the user who accepted
     */
    void sendOfferAcceptedEmail(String to, Long offerId, String userName);
    
    /**
     * Sends a notification when an offer is rejected.
     * @param to recipient email address (workshop)
     * @param offerId the offer ID
     */
    void sendOfferRejectedEmail(String to, Long offerId);
    
    /**
     * Sends a notification when a service is completed.
     * @param to recipient email address (user)
     * @param serviceBookingId the service booking ID
     * @param workshopName name of the workshop
     */
    void sendServiceCompletedEmail(String to, Long serviceBookingId, String workshopName);
    
    /**
     * Sends a notification when pickup is confirmed.
     * @param to recipient email address (workshop)
     * @param serviceBookingId the service booking ID
     * @param userName name of the user
     */
    void sendPickupConfirmedEmail(String to, Long serviceBookingId, String userName);
    
    /**
     * Sends a reminder for an upcoming service (24h before).
     * @param to recipient email address
     * @param serviceBookingId the service booking ID
     * @param scheduledDate the scheduled date/time
     * @param isUser true if sending to user, false if sending to workshop
     */
    void sendUpcomingServiceEmail(String to, Long serviceBookingId, java.time.LocalDateTime scheduledDate, boolean isUser);
}

