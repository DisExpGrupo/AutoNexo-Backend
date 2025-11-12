package com.atg.autonexo.backend.matching.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;

/**
 * Command to cancel a service booking.
 */
public record CancelServiceBookingCommand(
    Long serviceBookingId,
    UserId cancelledBy,
    String cancellationReason
) {
    public CancelServiceBookingCommand {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("ServiceBookingId must be valid");
        }
        if (cancelledBy == null) {
            throw new IllegalArgumentException("CancelledBy cannot be null");
        }
    }
}

