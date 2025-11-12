package com.atg.autonexo.backend.matching.domain.model.commands;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Command for a workshop to create an offer for a service request.
 */
public record CreateOfferCommand(
    Long serviceRequestId,
    WorkshopId workshopId,
    Money proposedPrice,
    LocalDateTime proposedDate,
    String message
) {
    public CreateOfferCommand {
        if (serviceRequestId == null || serviceRequestId <= 0) {
            throw new IllegalArgumentException("ServiceRequestId must be valid");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        if (proposedPrice == null) {
            throw new IllegalArgumentException("ProposedPrice cannot be null");
        }
    }
}

