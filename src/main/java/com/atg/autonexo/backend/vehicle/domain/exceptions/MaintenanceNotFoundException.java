package com.atg.autonexo.backend.vehicle.domain.exceptions;

/**
 * Exception thrown when a maintenance record is not found.
 */
public class MaintenanceNotFoundException extends RuntimeException {
    
    public MaintenanceNotFoundException(Long maintenanceId) {
        super("Maintenance not found with ID: " + maintenanceId);
    }
    
    public MaintenanceNotFoundException(String message) {
        super(message);
    }
}

