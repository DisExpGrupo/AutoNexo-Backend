package com.atg.autonexo.backend.vehicle.domain.exceptions;

/**
 * Exception thrown when an operation requires primary owner but user is not primary owner.
 */
public class OnlyPrimaryOwnerException extends RuntimeException {
    
    public OnlyPrimaryOwnerException(String operation) {
        super("Only primary owner can " + operation);
    }
}

