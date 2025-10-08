package com.atg.autonexo.backend.workshop.domain.exceptions;

/**
 * Exception thrown when a service template is not found within a workshop
 */
public class ServiceTemplateNotFoundException extends RuntimeException {
    
    public ServiceTemplateNotFoundException(Long serviceTemplateId) {
        super("Service template with ID " + serviceTemplateId + " not found");
    }
    
    public ServiceTemplateNotFoundException(String code) {
        super("Service template with code " + code + " not found");
    }
    
    public ServiceTemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

