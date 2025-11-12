package com.atg.autonexo.backend.vehicle.domain.exceptions;

/**
 * Exception thrown when a user tries to access a vehicle they don't own or are not authorized for.
 */
public class UnauthorizedVehicleAccessException extends RuntimeException {
    
    public UnauthorizedVehicleAccessException(Long userId, Long vehicleId) {
        super("User " + userId + " is not authorized to access vehicle " + vehicleId);
    }
    
    public UnauthorizedVehicleAccessException(String message) {
        super(message);
    }
}

