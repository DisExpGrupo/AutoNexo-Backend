package com.atg.autonexo.backend.matching.domain.model.valueobjects;

/**
 * Status of an offer sent by a workshop.
 */
public enum OfferStatus {
    PENDING,    // Waiting for response
    ACCEPTED,   // Accepted by the user (converts to ServiceBooking)
    REJECTED,   // Rejected by the user
    EXPIRED,    // Automatically expired after 3 days
    WITHDRAWN   // Withdrawn by the workshop
}

