package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when a location is not found within a workshop
 */
public class LocationNotFoundException extends RuntimeException {
    
    public LocationNotFoundException(Long locationId) {
        super("Location with ID " + locationId + " not found");
    }
    
    public LocationNotFoundException(String message) {
        super(message);
    }
    
    public LocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

