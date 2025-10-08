package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when a workshop is not found by ID
 */
public class WorkshopNotFoundException extends RuntimeException {
    
    public WorkshopNotFoundException(Long workshopId) {
        super("Workshop with ID " + workshopId + " not found");
    }
    
    public WorkshopNotFoundException(String message) {
        super(message);
    }
    
    public WorkshopNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

