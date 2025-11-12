package com.atg.autonexo.backend.vehicle.application.internal.services;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.notifications.domain.services.EmailService;
import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleBrand;
import com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories.VehicleBrandRepository;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleRepository;

/**
 * Service for sending maintenance reminders via email.
 * Runs daily to check for vehicles that need maintenance.
 */
@Service
public class MaintenanceReminderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceReminderService.class);
    
    private static final int MILEAGE_INTERVAL_KM = 5000;
    private static final int TIME_INTERVAL_MONTHS = 6;
    private static final int REMINDER_THRESHOLD_KM = 500; // Remind 500km before due
    private static final int REMINDER_THRESHOLD_DAYS = 14; // Remind 14 days before due
    
    private final VehicleRepository vehicleRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VehicleBrandRepository vehicleBrandRepository;
    
    public MaintenanceReminderService(
            VehicleRepository vehicleRepository,
            MaintenanceRepository maintenanceRepository,
            UserRepository userRepository,
            EmailService emailService,
            VehicleBrandRepository vehicleBrandRepository) {
        this.vehicleRepository = vehicleRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.vehicleBrandRepository = vehicleBrandRepository;
    }
    
    /**
     * Scheduled task that runs daily at 8 AM to check for maintenance reminders.
     */
    @Scheduled(cron = "0 0 8 * * *") // Every day at 8 AM
    @Transactional(readOnly = true)
    public void checkAndSendReminders() {
        LOGGER.info("Starting maintenance reminder check");
        
        try {
            // Get all active vehicles
            List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(Vehicle::isActive)
                .toList();
            
            int remindersSent = 0;
            for (Vehicle vehicle : vehicles) {
                if (shouldSendMileageReminder(vehicle)) {
                    sendMileageReminder(vehicle);
                    remindersSent++;
                }
                
                if (shouldSendTimeReminder(vehicle)) {
                    sendTimeReminder(vehicle);
                    remindersSent++;
                }
            }
            
            LOGGER.info("Maintenance reminder check completed. Sent {} reminders", remindersSent);
        } catch (Exception e) {
            LOGGER.error("Error during maintenance reminder check", e);
        }
    }
    
    /**
     * Checks if a mileage-based reminder should be sent.
     */
    private boolean shouldSendMileageReminder(Vehicle vehicle) {
        // Get last maintenance for this vehicle
        List<Maintenance> maintenances = maintenanceRepository.findByVehicleIdOrderByDateDesc(vehicle.getId());
        
        if (maintenances.isEmpty()) {
            // No maintenance history, check if vehicle has reached initial reminder threshold
            return vehicle.getCurrentMileage().value() >= MILEAGE_INTERVAL_KM - REMINDER_THRESHOLD_KM;
        }
        
        Maintenance lastMaintenance = maintenances.stream()
            .filter(m -> m.isConfirmed())
            .findFirst()
            .orElse(null);
        
        if (lastMaintenance == null) {
            return false;
        }
        
        int nextMaintenanceMileage = lastMaintenance.getMileage().value() + MILEAGE_INTERVAL_KM;
        int remainingKm = nextMaintenanceMileage - vehicle.getCurrentMileage().value();
        
        return remainingKm <= REMINDER_THRESHOLD_KM && remainingKm > 0;
    }
    
    /**
     * Checks if a time-based reminder should be sent.
     */
    private boolean shouldSendTimeReminder(Vehicle vehicle) {
        List<Maintenance> maintenances = maintenanceRepository.findByVehicleIdOrderByDateDesc(vehicle.getId());
        
        if (maintenances.isEmpty()) {
            return false;
        }
        
        Maintenance lastMaintenance = maintenances.stream()
            .filter(m -> m.isConfirmed())
            .findFirst()
            .orElse(null);
        
        if (lastMaintenance == null) {
            return false;
        }
        
        LocalDate nextMaintenanceDate = lastMaintenance.getMaintenanceDate().plusMonths(TIME_INTERVAL_MONTHS);
        LocalDate reminderDate = nextMaintenanceDate.minusDays(REMINDER_THRESHOLD_DAYS);
        
        return LocalDate.now().isAfter(reminderDate) && LocalDate.now().isBefore(nextMaintenanceDate);
    }
    
    /**
     * Sends a mileage-based maintenance reminder email.
     */
    private void sendMileageReminder(Vehicle vehicle) {
        try {
            Long ownerId = vehicle.getPrimaryOwnerId().id();
            User owner = userRepository.findById(ownerId)
                .orElse(null);
            
            if (owner == null) {
                LOGGER.warn("Owner not found for vehicle {} - Owner ID: {}", vehicle.getId(), ownerId);
                return;
            }
            
            // Calculate remaining kilometers
            List<Maintenance> maintenances = maintenanceRepository.findByVehicleIdOrderByDateDesc(vehicle.getId());
            int remainingKm = MILEAGE_INTERVAL_KM;
            if (!maintenances.isEmpty()) {
                Maintenance lastMaintenance = maintenances.stream()
                    .filter(m -> m.isConfirmed())
                    .findFirst()
                    .orElse(null);
                if (lastMaintenance != null) {
                    int nextMaintenanceMileage = lastMaintenance.getMileage().value() + MILEAGE_INTERVAL_KM;
                    remainingKm = nextMaintenanceMileage - vehicle.getCurrentMileage().value();
                }
            }
            
            String details = String.format("Your vehicle is approaching %d km since last maintenance. Approximately %d km remaining until next service.", 
                MILEAGE_INTERVAL_KM, remainingKm);
            
            // Resolve brand name
            String brandName = vehicleBrandRepository.findById(vehicle.getBrandId())
                .map(VehicleBrand::getName)
                .orElse("Unknown Brand");
            
            emailService.sendMaintenanceReminderEmail(
                owner.getEmail(),
                brandName,
                vehicle.getModel(),
                vehicle.getYear(),
                "mileage",
                details
            );
            
            LOGGER.info("Mileage reminder sent for vehicle {} to {}", vehicle.getId(), owner.getEmail());
        } catch (Exception e) {
            LOGGER.error("Error sending mileage reminder for vehicle {}", vehicle.getId(), e);
        }
    }
    
    /**
     * Sends a time-based maintenance reminder email.
     */
    private void sendTimeReminder(Vehicle vehicle) {
        try {
            Long ownerId = vehicle.getPrimaryOwnerId().id();
            User owner = userRepository.findById(ownerId)
                .orElse(null);
            
            if (owner == null) {
                LOGGER.warn("Owner not found for vehicle {} - Owner ID: {}", vehicle.getId(), ownerId);
                return;
            }
            
            List<Maintenance> maintenances = maintenanceRepository.findByVehicleIdOrderByDateDesc(vehicle.getId());
            if (maintenances.isEmpty()) {
                return;
            }
            
            Maintenance lastMaintenance = maintenances.stream()
                .filter(m -> m.isConfirmed())
                .findFirst()
                .orElse(null);
            
            if (lastMaintenance == null) {
                return;
            }
            
            LocalDate nextMaintenanceDate = lastMaintenance.getMaintenanceDate().plusMonths(TIME_INTERVAL_MONTHS);
            long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextMaintenanceDate);
            
            String details = String.format("Your vehicle is due for maintenance in approximately %d days (by %s).", 
                daysUntilDue, nextMaintenanceDate.toString());
            
            // Resolve brand name
            String brandName = vehicleBrandRepository.findById(vehicle.getBrandId())
                .map(VehicleBrand::getName)
                .orElse("Unknown Brand");
            
            emailService.sendMaintenanceReminderEmail(
                owner.getEmail(),
                brandName,
                vehicle.getModel(),
                vehicle.getYear(),
                "time",
                details
            );
            
            LOGGER.info("Time-based reminder sent for vehicle {} to {}", vehicle.getId(), owner.getEmail());
        } catch (Exception e) {
            LOGGER.error("Error sending time reminder for vehicle {}", vehicle.getId(), e);
        }
    }
}

