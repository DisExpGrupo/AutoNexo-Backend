package com.atg.autonexo.backend.trust.domain.model.commands;

/**
 * Command to create a review for a service booking.
 */
public record CreateReviewCommand(
    Long serviceBookingId,
    Integer rating,
    String comment
) {
    public CreateReviewCommand {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("Service booking ID must be valid");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}

