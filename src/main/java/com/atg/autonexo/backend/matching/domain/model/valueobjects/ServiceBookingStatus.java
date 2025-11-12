package com.atg.autonexo.backend.matching.domain.model.valueobjects;

/**
 * Status of a service booking (scheduled service).
 */
public enum ServiceBookingStatus {
    PENDING_SCHEDULE,  // Offer accepted, negotiating date/time
    SCHEDULED,         // Date/time confirmed
    IN_PROGRESS,       // Service in progress (optional, can be skipped)
    COMPLETED,         // Workshop marked as complete
    PENDING_PICKUP,    // Waiting for user pickup confirmation
    PICKED_UP,         // User confirmed pickup (final state)
    CANCELLED          // Cancelled by user or workshop
}

