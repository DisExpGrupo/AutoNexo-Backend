package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when business registration (RUC) validation fails
 */
public class InvalidBusinessRegistrationException extends RuntimeException {
    
    public InvalidBusinessRegistrationException(String ruc) {
        super("Invalid business registration (RUC): " + ruc);
    }
    
    public InvalidBusinessRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

