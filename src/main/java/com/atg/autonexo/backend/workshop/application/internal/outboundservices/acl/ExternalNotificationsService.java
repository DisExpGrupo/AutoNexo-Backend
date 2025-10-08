package com.atg.autonexo.backend.workshop.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.workshop.domain.services.NotificationService;

/**
 * Anti-Corruption Layer implementation for external notification services.
 * This service abstracts away the details of how notifications are sent
 * (email, SMS, push notifications, etc.) from the domain logic.
 * 
 * For MVP: This is a stub implementation that logs notifications.
 * In production: Connect to SendGrid, AWS SES, Twilio, Firebase, etc.
 */
@Service
public class ExternalNotificationsService implements NotificationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalNotificationsService.class);
    
    @Override
    public void sendInvitationEmail(String email, String invitationCode, String workshopName) {
        LOGGER.info("===== SENDING INVITATION EMAIL =====");
        LOGGER.info("To: {}", email);
        LOGGER.info("Workshop: {}", workshopName);
        LOGGER.info("Invitation Code: {}", invitationCode);
        LOGGER.info("====================================");
        
        // TODO: In production, integrate with email service provider:
        // - SendGrid: sendGridClient.send(email, template, data)
        // - AWS SES: sesClient.sendEmail(...)
        // - Mailgun: mailgunClient.sendMessage(...)
        
        // Example email template:
        // Subject: "You've been invited to join {workshopName}"
        // Body: "Use code {invitationCode} to accept your invitation"
        // Link: "https://autonexo.com/invitations/accept?code={invitationCode}"
    }
    
    @Override
    public void sendInvitationExpiredNotification(String email, String workshopName) {
        LOGGER.info("===== INVITATION EXPIRED =====");
        LOGGER.info("To: {}", email);
        LOGGER.info("Workshop: {}", workshopName);
        LOGGER.info("==============================");
        
        // TODO: Implement expiration notification
    }
}
