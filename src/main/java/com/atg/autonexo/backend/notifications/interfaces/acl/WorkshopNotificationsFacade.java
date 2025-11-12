package com.atg.autonexo.backend.notifications.interfaces.acl;

import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.notifications.domain.services.EmailService;
import com.atg.autonexo.backend.workshop.domain.services.NotificationService;

/**
 * Anti-Corruption Layer facade that implements Workshop's NotificationService interface.
 * This facade bridges the Workshop context with the Notifications bounded context.
 */
@Service
public class WorkshopNotificationsFacade implements NotificationService {
    
    private final EmailService emailService;
    
    public WorkshopNotificationsFacade(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Override
    public void sendInvitationEmail(String email, String invitationCode, String workshopName) {
        emailService.sendWorkshopInvitationEmail(email, invitationCode, workshopName, null);
    }
    
    @Override
    public void sendInvitationExpiredNotification(String email, String workshopName) {
        emailService.sendInvitationExpiredEmail(email, workshopName);
    }
}

