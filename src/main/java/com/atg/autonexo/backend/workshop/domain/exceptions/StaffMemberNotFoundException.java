package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when a staff member is not found within a workshop
 */
public class StaffMemberNotFoundException extends RuntimeException {
    
    public StaffMemberNotFoundException(Long staffMemberId) {
        super("Staff member with ID " + staffMemberId + " not found");
    }
    
    public StaffMemberNotFoundException(String message) {
        super(message);
    }
    
    public StaffMemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

