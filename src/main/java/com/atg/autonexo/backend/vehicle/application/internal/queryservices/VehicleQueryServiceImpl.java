package com.atg.autonexo.backend.vehicle.application.internal.queryservices;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.vehicle.domain.exceptions.UnauthorizedVehicleAccessException;
import com.atg.autonexo.backend.vehicle.domain.exceptions.VehicleNotFoundException;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetUserVehiclesQuery;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetVehicleByIdQuery;
import com.atg.autonexo.backend.vehicle.domain.services.VehicleQueryService;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleOwnershipRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleRepository;

/**
 * Implementation of VehicleQueryService.
 * Handles all read operations for the Vehicle aggregate.
 */
@Service
@Transactional(readOnly = true)
public class VehicleQueryServiceImpl implements VehicleQueryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleQueryServiceImpl.class);
    
    private final VehicleRepository vehicleRepository;
    private final VehicleOwnershipRepository ownershipRepository;
    
    public VehicleQueryServiceImpl(
            VehicleRepository vehicleRepository,
            VehicleOwnershipRepository ownershipRepository) {
        this.vehicleRepository = vehicleRepository;
        this.ownershipRepository = ownershipRepository;
    }
    
    @Override
    public List<Vehicle> handle(GetUserVehiclesQuery query) {
        LOGGER.info("Getting vehicles for user ID: {}", query.userId());
        
        // Get all vehicle IDs where user is owner or authorized
        List<Long> vehicleIds = ownershipRepository.findVehicleIdsByUserId(query.userId());
        
        if (vehicleIds.isEmpty()) {
            return List.of();
        }
        
        // Fetch vehicles
        List<Vehicle> vehicles = vehicleIds.stream()
            .map(vehicleRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(Vehicle::isActive)
            .collect(Collectors.toList());
        
        LOGGER.info("Found {} vehicles for user {}", vehicles.size(), query.userId());
        return vehicles;
    }
    
    @Override
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        LOGGER.info("Getting vehicle ID: {} for user ID: {}", query.vehicleId(), query.userId());
        
        Vehicle vehicle = vehicleRepository.findById(query.vehicleId())
            .orElseThrow(() -> new VehicleNotFoundException(query.vehicleId()));
        
        if (!vehicle.isActive()) {
            throw new VehicleNotFoundException(query.vehicleId());
        }
        
        // Check authorization
        if (!ownershipRepository.isUserAuthorized(query.vehicleId(), query.userId())) {
            throw new UnauthorizedVehicleAccessException(query.userId(), query.vehicleId());
        }
        
        return Optional.of(vehicle);
    }
}

