package com.atg.autonexo.backend.notifications.interfaces.acl;

import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.iam.application.internal.outboundservices.notifications.NotificationService;
import com.atg.autonexo.backend.notifications.domain.services.EmailService;

/**
 * Anti-Corruption Layer facade that implements IAM's NotificationService interface.
 * This facade bridges the IAM context with the Notifications bounded context.
 */
@Service
public class IamNotificationsFacade implements NotificationService {
    
    private final EmailService emailService;
    
    public IamNotificationsFacade(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Override
    public void sendPasswordResetToken(String email, String token) {
        emailService.sendPasswordResetEmail(email, token, null);
    }
    
    @Override
    public void sendEmailVerificationToken(String email, String token) {
        emailService.sendEmailVerificationEmail(email, token, null);
    }
}

