package com.atg.autonexo.backend.notifications.application.internal.commandservices;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.notifications.domain.services.EmailService;
import com.atg.autonexo.backend.notifications.infrastructure.mail.EmailProperties;
import com.atg.autonexo.backend.notifications.infrastructure.mail.EmailTemplateService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementation of EmailService using JavaMailSender.
 * Sends HTML emails with templates for different notification types.
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private final EmailTemplateService templateService;
    
    public EmailServiceImpl(JavaMailSender mailSender, EmailProperties emailProperties, EmailTemplateService templateService) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
        this.templateService = templateService;
    }
    
    @Override
    public void sendPasswordResetEmail(String to, String token, String resetUrl) {
        String url = resetUrl != null ? resetUrl : emailProperties.getBaseUrl() + "/reset-password?token=" + token;
        String subject = "Password Reset Request - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("resetUrl", url);
        
        String htmlBody = templateService.processTemplate("password-reset", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendEmailVerificationEmail(String to, String token, String verificationUrl) {
        String url = verificationUrl != null ? verificationUrl : emailProperties.getBaseUrl() + "/verify-email?token=" + token;
        String subject = "Verify Your Email Address - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("verificationUrl", url);
        
        String htmlBody = templateService.processTemplate("email-verification", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendWorkshopInvitationEmail(String to, String invitationCode, String workshopName, String invitationUrl) {
        String url = invitationUrl != null ? invitationUrl : emailProperties.getBaseUrl() + "/invitations/accept?code=" + invitationCode;
        String subject = "You've been invited to join " + workshopName + " - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("invitationCode", invitationCode);
        variables.put("workshopName", workshopName);
        variables.put("invitationUrl", url);
        
        String htmlBody = templateService.processTemplate("workshop-invitation", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendInvitationExpiredEmail(String to, String workshopName) {
        String subject = "Invitation Expired - " + workshopName + " - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("workshopName", workshopName);
        
        String htmlBody = templateService.processTemplate("invitation-expired", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendMaintenanceReminderEmail(String to, String vehicleBrand, String vehicleModel, 
                                            Integer vehicleYear, String reminderType, String details) {
        String vehicleName = String.format("%s %s %d", vehicleBrand, vehicleModel, vehicleYear);
        String subject = "Maintenance Reminder - " + vehicleName + " - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("vehicleName", vehicleName);
        variables.put("reminderType", reminderType);
        variables.put("details", details);
        
        String htmlBody = templateService.processTemplate("maintenance-reminder", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendNewOfferEmail(String to, Long serviceRequestId, Long offerId, String workshopName) {
        String subject = "New Offer Received - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("workshopName", workshopName);
        variables.put("serviceRequestId", String.valueOf(serviceRequestId));
        variables.put("offerId", String.valueOf(offerId));
        variables.put("viewUrl", emailProperties.getBaseUrl() + "/service-requests/" + serviceRequestId);
        
        String htmlBody = templateService.processTemplate("new-offer", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendOfferAcceptedEmail(String to, Long offerId, String userName) {
        String subject = "Offer Accepted - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("offerId", String.valueOf(offerId));
        variables.put("viewUrl", emailProperties.getBaseUrl() + "/service-bookings");
        
        String htmlBody = templateService.processTemplate("offer-accepted", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendOfferRejectedEmail(String to, Long offerId) {
        String subject = "Offer Rejected - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("offerId", String.valueOf(offerId));
        
        String htmlBody = templateService.processTemplate("offer-rejected", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendServiceCompletedEmail(String to, Long serviceBookingId, String workshopName) {
        String subject = "Service Completed - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("workshopName", workshopName);
        variables.put("serviceBookingId", String.valueOf(serviceBookingId));
        variables.put("viewUrl", emailProperties.getBaseUrl() + "/service-bookings/" + serviceBookingId);
        
        String htmlBody = templateService.processTemplate("service-completed", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendPickupConfirmedEmail(String to, Long serviceBookingId, String userName) {
        String subject = "Pickup Confirmed - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("serviceBookingId", String.valueOf(serviceBookingId));
        
        String htmlBody = templateService.processTemplate("pickup-confirmed", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendUpcomingServiceEmail(String to, Long serviceBookingId, java.time.LocalDateTime scheduledDate, boolean isUser) {
        String subject = "Upcoming Service Reminder - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("serviceBookingId", String.valueOf(serviceBookingId));
        variables.put("scheduledDate", scheduledDate.toString());
        variables.put("isUser", String.valueOf(isUser));
        variables.put("viewUrl", emailProperties.getBaseUrl() + "/service-bookings/" + serviceBookingId);
        
        String htmlBody = templateService.processTemplate("upcoming-service", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    @Override
    public void sendServiceRequestMatchNotification(String to, Long serviceRequestId, String services, Double distanceKm) {
        String subject = "Nueva Solicitud de Servicio Disponible - Autonexo";
        
        Map<String, String> variables = new HashMap<>();
        variables.put("serviceRequestId", serviceRequestId.toString());
        variables.put("services", services);
        variables.put("distance", String.format("%.1f km", distanceKm));
        variables.put("requestUrl", emailProperties.getBaseUrl() + "/workshops/requests/" + serviceRequestId);
        
        String htmlBody = templateService.processTemplate("service-request-match", variables);
        sendEmail(to, subject, htmlBody);
    }
    
    /**
     * Sends an HTML email using JavaMailSender.
     */
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(emailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML
            
            mailSender.send(message);
            LOGGER.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error sending email to: {}", to, e);
            throw new RuntimeException("Unexpected error sending email", e);
        }
    }
    
}

