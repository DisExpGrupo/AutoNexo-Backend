package com.atg.autonexo.backend.vehicle.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.vehicle.domain.exceptions.MaintenanceNotFoundException;
import com.atg.autonexo.backend.vehicle.domain.exceptions.UnauthorizedVehicleAccessException;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetMaintenanceByIdQuery;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetPendingMaintenancesQuery;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetVehicleMaintenanceHistoryQuery;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.MaintenanceStatus;
import com.atg.autonexo.backend.vehicle.domain.services.MaintenanceQueryService;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleOwnershipRepository;

/**
 * Implementation of MaintenanceQueryService.
 * Handles all read operations for the Maintenance aggregate.
 */
@Service
@Transactional(readOnly = true)
public class MaintenanceQueryServiceImpl implements MaintenanceQueryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceQueryServiceImpl.class);
    
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleOwnershipRepository ownershipRepository;
    
    public MaintenanceQueryServiceImpl(
            MaintenanceRepository maintenanceRepository,
            VehicleOwnershipRepository ownershipRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.ownershipRepository = ownershipRepository;
    }
    
    @Override
    public List<Maintenance> handle(GetVehicleMaintenanceHistoryQuery query) {
        LOGGER.info("Getting maintenance history for vehicle ID: {} by user {}", query.vehicleId(), query.userId());
        
        // Verify user has access to vehicle
        if (!ownershipRepository.isUserAuthorized(query.vehicleId(), query.userId())) {
            throw new UnauthorizedVehicleAccessException(query.userId(), query.vehicleId());
        }
        
        List<Maintenance> maintenances = maintenanceRepository.findByVehicleIdOrderByDateDesc(query.vehicleId());
        LOGGER.info("Found {} maintenance records for vehicle {}", maintenances.size(), query.vehicleId());
        
        return maintenances;
    }
    
    @Override
    public Optional<Maintenance> handle(GetMaintenanceByIdQuery query) {
        LOGGER.info("Getting maintenance ID: {} by user {}", query.maintenanceId(), query.userId());
        
        Maintenance maintenance = maintenanceRepository.findById(query.maintenanceId())
            .orElseThrow(() -> new MaintenanceNotFoundException(query.maintenanceId()));
        
        // Verify user has access to vehicle
        if (!ownershipRepository.isUserAuthorized(maintenance.getVehicleId(), query.userId())) {
            throw new UnauthorizedVehicleAccessException(query.userId(), maintenance.getVehicleId());
        }
        
        return Optional.of(maintenance);
    }
    
    @Override
    public List<Maintenance> handle(GetPendingMaintenancesQuery query) {
        LOGGER.info("Getting pending maintenances for user ID: {}", query.userId());
        
        List<Maintenance> pendingMaintenances = maintenanceRepository.findPendingByUserId(
            query.userId(), 
            MaintenanceStatus.PENDING_CONFIRMATION
        );
        
        LOGGER.info("Found {} pending maintenances for user {}", pendingMaintenances.size(), query.userId());
        return pendingMaintenances;
    }
}

