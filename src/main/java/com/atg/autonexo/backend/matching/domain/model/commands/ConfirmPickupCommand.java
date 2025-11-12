package com.atg.autonexo.backend.matching.domain.model.commands;

/**
 * Command for a user to confirm pickup of their vehicle.
 */
public record ConfirmPickupCommand(
    Long serviceBookingId,
    Long userId
) {
    public ConfirmPickupCommand {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("ServiceBookingId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

