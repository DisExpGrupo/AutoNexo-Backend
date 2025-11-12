package com.atg.autonexo.backend.matching.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Command for a workshop to reject a service request.
 */
public record RejectServiceRequestCommand(
    Long serviceRequestId,
    WorkshopId workshopId
) {
    public RejectServiceRequestCommand {
        if (serviceRequestId == null || serviceRequestId <= 0) {
            throw new IllegalArgumentException("ServiceRequestId must be valid");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
    }
}

