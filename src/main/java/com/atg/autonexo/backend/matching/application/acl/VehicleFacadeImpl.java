package com.atg.autonexo.backend.matching.application.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.interfaces.acl.VehicleFacade;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.vehicle.domain.exceptions.VehicleNotFoundException;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleOwnershipRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleRepository;

/**
 * Implementation of VehicleFacade.
 * Provides ACL for Matching & Booking context to access Vehicle context data.
 */
@Service
@Transactional(readOnly = true)
public class VehicleFacadeImpl implements VehicleFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleFacadeImpl.class);
    
    private final VehicleRepository vehicleRepository;
    private final VehicleOwnershipRepository ownershipRepository;
    
    public VehicleFacadeImpl(
            VehicleRepository vehicleRepository,
            VehicleOwnershipRepository ownershipRepository) {
        this.vehicleRepository = vehicleRepository;
        this.ownershipRepository = ownershipRepository;
    }
    
    @Override
    public VehicleInfo getVehicleInfo(Long vehicleId, UserId userId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        
        // Verify user has access
        if (!ownershipRepository.isUserAuthorized(vehicleId, userId.id())) {
            throw new SecurityException("User does not have access to this vehicle");
        }
        
        return new VehicleInfo(
            vehicle.getId(),
            vehicle.getBrand(),
            vehicle.getModel(),
            vehicle.getYear(),
            vehicle.getPrimaryOwnerId()
        );
    }
    
    @Override
    public boolean userOwnsVehicle(Long vehicleId, UserId userId) {
        return ownershipRepository.isUserAuthorized(vehicleId, userId.id());
    }
}

