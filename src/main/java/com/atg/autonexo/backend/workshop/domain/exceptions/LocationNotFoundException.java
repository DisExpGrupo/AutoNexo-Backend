package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when a location is not found.
 */
public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(Long locationId) {
        super("Location not found with ID: " + locationId);
    }
}
