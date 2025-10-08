package com.atg.autonexo.backend.workshop.domain.services;

/**
 * Domain service interface for sending notifications.
 * This is an anti-corruption layer to external notification systems.
 */
public interface NotificationService {
    
    /**
     * Sends an invitation email to a potential staff member
     * @param email recipient email address
     * @param invitationCode the invitation code to send
     * @param workshopName name of the workshop extending the invitation
     */
    void sendInvitationEmail(String email, String invitationCode, String workshopName);
    
    /**
     * Sends a notification when an invitation expires
     * @param email recipient email address
     * @param workshopName name of the workshop
     */
    void sendInvitationExpiredNotification(String email, String workshopName);
}

