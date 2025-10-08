package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when attempting to create a workshop that already exists for a user
 */
public class WorkshopAlreadyExistsException extends RuntimeException {
    
    public WorkshopAlreadyExistsException(Long ownerUserId) {
        super("Workshop already exists for user ID " + ownerUserId);
    }
    
    public WorkshopAlreadyExistsException(String message) {
        super(message);
    }
    
    public WorkshopAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

