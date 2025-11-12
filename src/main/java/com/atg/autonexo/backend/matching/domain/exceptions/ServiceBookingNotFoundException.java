package com.atg.autonexo.backend.matching.domain.exceptions;

/**
 * Exception thrown when a service booking is not found.
 */
public class ServiceBookingNotFoundException extends RuntimeException {
    
    public ServiceBookingNotFoundException(Long serviceBookingId) {
        super("Service booking not found with ID: " + serviceBookingId);
    }
    
    public ServiceBookingNotFoundException(String message) {
        super(message);
    }
}

