package com.atg.autonexo.backend.vehicle.domain.exceptions;

/**
 * Exception thrown when a vehicle is not found.
 */
public class VehicleNotFoundException extends RuntimeException {
    
    public VehicleNotFoundException(Long vehicleId) {
        super("Vehicle not found with ID: " + vehicleId);
    }
    
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

