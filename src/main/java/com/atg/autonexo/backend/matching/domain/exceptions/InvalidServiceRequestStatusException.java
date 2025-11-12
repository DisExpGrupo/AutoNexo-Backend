package com.atg.autonexo.backend.matching.domain.exceptions;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceRequestStatus;

/**
 * Exception thrown when an operation is attempted on a service request with an invalid status.
 */
public class InvalidServiceRequestStatusException extends RuntimeException {
    
    public InvalidServiceRequestStatusException(Long serviceRequestId, ServiceRequestStatus currentStatus, String operation) {
        super(String.format("Cannot %s service request %d: current status is %s", operation, serviceRequestId, currentStatus));
    }
    
    public InvalidServiceRequestStatusException(String message) {
        super(message);
    }
}

