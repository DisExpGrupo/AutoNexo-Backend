package com.atg.autonexo.backend.vehicle.application.internal.commandservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.atg.autonexo.backend.vehicle.domain.exceptions.MaintenanceNotFoundException;
import com.atg.autonexo.backend.vehicle.domain.exceptions.UnauthorizedVehicleAccessException;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.commands.ConfirmMaintenanceCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.CreateMaintenanceCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.RejectMaintenanceCommand;
import com.atg.autonexo.backend.vehicle.domain.services.MaintenanceCommandService;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleOwnershipRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleRepository;

/**
 * Implementation of MaintenanceCommandService.
 * Handles all write operations for the Maintenance aggregate.
 */
@Service
@Transactional
public class MaintenanceCommandServiceImpl implements MaintenanceCommandService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceCommandServiceImpl.class);
    
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleOwnershipRepository ownershipRepository;
    
    public MaintenanceCommandServiceImpl(
            MaintenanceRepository maintenanceRepository,
            VehicleRepository vehicleRepository,
            VehicleOwnershipRepository ownershipRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
        this.ownershipRepository = ownershipRepository;
    }
    
    @Override
    public Maintenance handle(CreateMaintenanceCommand command) {
        Long userId = getCurrentUserId();
        LOGGER.info("Creating maintenance for vehicle ID: {} by user {}", command.vehicleId(), userId);
        
        // Verify user has access to vehicle
        if (!ownershipRepository.isUserAuthorized(command.vehicleId(), userId)) {
            throw new UnauthorizedVehicleAccessException(userId, command.vehicleId());
        }
        
        // Verify vehicle exists and is active
        vehicleRepository.findById(command.vehicleId())
            .orElseThrow(() -> new com.atg.autonexo.backend.vehicle.domain.exceptions.VehicleNotFoundException(command.vehicleId()));
        
        // Create maintenance
        Maintenance maintenance = new Maintenance(
            command.vehicleId(),
            command.maintenanceDate(),
            command.mileage(),
            command.observations(),
            command.workshopId()
        );
        
        // Add services
        if (command.services() != null) {
            for (CreateMaintenanceCommand.ServicePerformedData serviceData : command.services()) {
                maintenance.addService(serviceData.serviceType(), serviceData.description(), serviceData.cost());
            }
        }
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        
        // Update maintenance ID for all services
        savedMaintenance.updateServicesMaintenanceId();
        savedMaintenance = maintenanceRepository.save(savedMaintenance);
        
        LOGGER.info("Maintenance created successfully with ID: {}", savedMaintenance.getId());
        
        return savedMaintenance;
    }
    
    @Override
    public void handle(ConfirmMaintenanceCommand command) {
        Long userId = getCurrentUserId();
        LOGGER.info("Confirming maintenance ID: {} by user {}", command.maintenanceId(), userId);
        
        Maintenance maintenance = maintenanceRepository.findById(command.maintenanceId())
            .orElseThrow(() -> new MaintenanceNotFoundException(command.maintenanceId()));
        
        // Verify user has access to vehicle
        if (!ownershipRepository.isUserAuthorized(maintenance.getVehicleId(), userId)) {
            throw new UnauthorizedVehicleAccessException(userId, maintenance.getVehicleId());
        }
        
        maintenance.confirm();
        maintenanceRepository.save(maintenance);
        
        LOGGER.info("Maintenance confirmed successfully");
    }
    
    @Override
    public void handle(RejectMaintenanceCommand command) {
        Long userId = getCurrentUserId();
        LOGGER.info("Rejecting maintenance ID: {} by user {}", command.maintenanceId(), userId);
        
        Maintenance maintenance = maintenanceRepository.findById(command.maintenanceId())
            .orElseThrow(() -> new MaintenanceNotFoundException(command.maintenanceId()));
        
        // Verify user has access to vehicle
        if (!ownershipRepository.isUserAuthorized(maintenance.getVehicleId(), userId)) {
            throw new UnauthorizedVehicleAccessException(userId, maintenance.getVehicleId());
        }
        
        maintenance.reject();
        maintenanceRepository.save(maintenance);
        
        LOGGER.info("Maintenance rejected successfully");
    }
    
    /**
     * Helper method to get current authenticated user ID from SecurityContext.
     */
    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new SecurityException("User is not authenticated");
        }
        
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        
        throw new SecurityException("Unable to extract user ID from authentication");
    }
}

