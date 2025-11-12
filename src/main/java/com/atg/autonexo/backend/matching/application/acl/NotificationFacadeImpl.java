package com.atg.autonexo.backend.matching.application.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.matching.interfaces.acl.NotificationFacade;
import com.atg.autonexo.backend.notifications.domain.services.EmailService;

/**
 * Implementation of NotificationFacade.
 * Provides ACL for Matching & Booking context to send notifications.
 */
@Service
public class NotificationFacadeImpl implements NotificationFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationFacadeImpl.class);
    
    private final EmailService emailService;
    
    public NotificationFacadeImpl(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Override
    public void notifyOfferReceived(Long serviceRequestId, Long offerId, String userEmail) {
        LOGGER.info("Sending offer received notification to user: {}", userEmail);
        try {
            // Get workshop name from offer (would need to fetch offer, but for now use placeholder)
            emailService.sendNewOfferEmail(userEmail, serviceRequestId, offerId, "Workshop");
        } catch (Exception e) {
            LOGGER.error("Error sending offer received notification", e);
        }
    }
    
    @Override
    public void notifyOfferAccepted(Long offerId, String workshopEmail) {
        LOGGER.info("Sending offer accepted notification to workshop: {}", workshopEmail);
        try {
            emailService.sendOfferAcceptedEmail(workshopEmail, offerId, "Customer");
        } catch (Exception e) {
            LOGGER.error("Error sending offer accepted notification", e);
        }
    }
    
    @Override
    public void notifyOfferRejected(Long offerId, String workshopEmail) {
        LOGGER.info("Sending offer rejected notification to workshop: {}", workshopEmail);
        try {
            emailService.sendOfferRejectedEmail(workshopEmail, offerId);
        } catch (Exception e) {
            LOGGER.error("Error sending offer rejected notification", e);
        }
    }
    
    @Override
    public void notifyServiceCompleted(Long serviceBookingId, String userEmail) {
        LOGGER.info("Sending service completed notification to user: {}", userEmail);
        try {
            // Get workshop name (would need service booking, but for now use placeholder)
            emailService.sendServiceCompletedEmail(userEmail, serviceBookingId, "Workshop");
        } catch (Exception e) {
            LOGGER.error("Error sending service completed notification", e);
        }
    }
    
    @Override
    public void notifyPickupConfirmed(Long serviceBookingId, String workshopEmail) {
        LOGGER.info("Sending pickup confirmed notification to workshop: {}", workshopEmail);
        try {
            emailService.sendPickupConfirmedEmail(workshopEmail, serviceBookingId, "Customer");
        } catch (Exception e) {
            LOGGER.error("Error sending pickup confirmed notification", e);
        }
    }
    
    @Override
    public void notifyUpcomingService(Long serviceBookingId, String userEmail, String workshopEmail) {
        LOGGER.info("Sending upcoming service reminder to user: {} and workshop: {}", userEmail, workshopEmail);
        try {
            // Get scheduled date (would need service booking, but for now use placeholder)
            java.time.LocalDateTime scheduledDate = java.time.LocalDateTime.now().plusHours(24);
            emailService.sendUpcomingServiceEmail(userEmail, serviceBookingId, scheduledDate, true);
            emailService.sendUpcomingServiceEmail(workshopEmail, serviceBookingId, scheduledDate, false);
        } catch (Exception e) {
            LOGGER.error("Error sending upcoming service reminder", e);
        }
    }
}

