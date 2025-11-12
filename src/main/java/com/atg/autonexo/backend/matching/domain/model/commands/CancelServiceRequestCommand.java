package com.atg.autonexo.backend.matching.domain.model.commands;

/**
 * Command to cancel a service request.
 */
public record CancelServiceRequestCommand(
    Long serviceRequestId,
    Long userId
) {
    public CancelServiceRequestCommand {
        if (serviceRequestId == null || serviceRequestId <= 0) {
            throw new IllegalArgumentException("ServiceRequestId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

