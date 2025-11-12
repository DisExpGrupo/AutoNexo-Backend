package com.atg.autonexo.backend.matching.domain.model.commands;

import java.time.LocalDateTime;

/**
 * Command to confirm the scheduled date/time for a service booking.
 */
public record ConfirmScheduleCommand(
    Long serviceBookingId,
    LocalDateTime scheduledDate,
    Long userId
) {
    public ConfirmScheduleCommand {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("ServiceBookingId must be valid");
        }
        if (scheduledDate == null) {
            throw new IllegalArgumentException("ScheduledDate cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

