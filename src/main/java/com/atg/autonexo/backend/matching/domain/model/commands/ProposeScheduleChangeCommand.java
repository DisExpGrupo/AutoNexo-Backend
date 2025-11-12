package com.atg.autonexo.backend.matching.domain.model.commands;

import java.time.LocalDateTime;

/**
 * Command to propose a schedule change (mediación) for a service booking.
 */
public record ProposeScheduleChangeCommand(
    Long serviceBookingId,
    LocalDateTime newScheduledDate,
    Long userId
) {
    public ProposeScheduleChangeCommand {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("ServiceBookingId must be valid");
        }
        if (newScheduledDate == null) {
            throw new IllegalArgumentException("NewScheduledDate cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

