package com.atg.autonexo.backend.matching.domain.exceptions;

/**
 * Exception thrown when a service request is not found.
 */
public class ServiceRequestNotFoundException extends RuntimeException {
    
    public ServiceRequestNotFoundException(Long serviceRequestId) {
        super("Service request not found with ID: " + serviceRequestId);
    }
    
    public ServiceRequestNotFoundException(String message) {
        super(message);
    }
}

