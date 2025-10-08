package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when a workshop context is required but not found.
 * This typically happens when a user without a workshop tries to access workshop-specific endpoints.
 */
public class WorkshopContextNotFoundException extends RuntimeException {
    
    public WorkshopContextNotFoundException() {
        super("Workshop context not found. This operation requires an authenticated workshop user.");
    }
    
    public WorkshopContextNotFoundException(String message) {
        super(message);
    }
}

