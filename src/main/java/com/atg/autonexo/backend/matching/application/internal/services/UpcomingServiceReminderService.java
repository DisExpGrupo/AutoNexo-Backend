package com.atg.autonexo.backend.matching.application.internal.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceBooking;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceBookingStatus;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceBookingRepository;
import com.atg.autonexo.backend.matching.interfaces.acl.NotificationFacade;
import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

/**
 * Scheduled service for sending reminders about upcoming services.
 * Runs daily at 8 AM to send reminders for services scheduled in the next 24 hours.
 */
@Service
public class UpcomingServiceReminderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UpcomingServiceReminderService.class);
    
    private final ServiceBookingRepository serviceBookingRepository;
    private final NotificationFacade notificationFacade;
    private final UserRepository userRepository;
    private final WorkshopRepository workshopRepository;
    
    public UpcomingServiceReminderService(
            ServiceBookingRepository serviceBookingRepository,
            NotificationFacade notificationFacade,
            UserRepository userRepository,
            WorkshopRepository workshopRepository) {
        this.serviceBookingRepository = serviceBookingRepository;
        this.notificationFacade = notificationFacade;
        this.userRepository = userRepository;
        this.workshopRepository = workshopRepository;
    }
    
    /**
     * Scheduled task that runs daily at 8 AM to send reminders for upcoming services.
     */
    @Scheduled(cron = "0 0 8 * * ?") // Every day at 8 AM
    @Transactional(readOnly = true)
    public void sendUpcomingServiceReminders() {
        LOGGER.info("Starting upcoming service reminders check");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusHours(24);
            
            // Find services scheduled in the next 24 hours with SCHEDULED status
            List<ServiceBooking> upcomingBookings = serviceBookingRepository.findByScheduledDateAfterAndStatus(
                now,
                ServiceBookingStatus.SCHEDULED
            );
            
            int remindersSent = 0;
            for (ServiceBooking booking : upcomingBookings) {
                if (booking.getScheduledDate() != null && 
                    booking.getScheduledDate().isAfter(now) && 
                    booking.getScheduledDate().isBefore(tomorrow)) {
                    
                    try {
                        // Get user email
                        User user = userRepository.findById(booking.getUserId().id()).orElse(null);
                        String userEmail = user != null ? user.getEmail() : null;
                        
                        // Get workshop owner email
                        Workshop workshop = workshopRepository.findById(booking.getWorkshopId().id()).orElse(null);
                        String workshopEmail = null;
                        if (workshop != null) {
                            User owner = userRepository.findById(workshop.getOwnerUserId().id()).orElse(null);
                            workshopEmail = owner != null ? owner.getEmail() : null;
                        }
                        
                        if (userEmail != null && workshopEmail != null) {
                            notificationFacade.notifyUpcomingService(booking.getId(), userEmail, workshopEmail);
                            remindersSent++;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error sending reminder for booking {}", booking.getId(), e);
                    }
                }
            }
            
            LOGGER.info("Upcoming service reminders check completed. Found {} upcoming services", remindersSent);
        } catch (Exception e) {
            LOGGER.error("Error during upcoming service reminders check", e);
        }
    }
}

