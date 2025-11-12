package com.atg.autonexo.backend.matching.domain.model.valueobjects;

/**
 * Status of a service request.
 */
public enum ServiceRequestStatus {
    PENDING,      // Waiting for offers
    CANCELLED,    // Cancelled by the user
    COMPLETED,    // Converted to ServiceBooking and completed
    REJECTED      // Rejected by a workshop (only affects visibility for that workshop)
}

