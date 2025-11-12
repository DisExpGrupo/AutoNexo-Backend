package com.atg.autonexo.backend.trust.interfaces.rest.resources;

/**
 * Resource for creating a review.
 */
public record CreateReviewResource(
    Long serviceBookingId,
    Integer rating,
    String comment
) {}

