package com.atg.autonexo.backend.trust.domain.exceptions;

/**
 * Exception thrown when attempting to create a review for a service booking that is not completed or cancelled.
 */
public class InvalidServiceBookingStatusException extends RuntimeException {
    public InvalidServiceBookingStatusException(String currentStatus) {
        super(String.format("Cannot create review for service booking with status: %s. " +
            "Service must be COMPLETED or CANCELLED", currentStatus));
    }
}

