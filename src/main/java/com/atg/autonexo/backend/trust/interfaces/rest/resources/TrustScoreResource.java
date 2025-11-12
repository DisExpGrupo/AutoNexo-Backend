package com.atg.autonexo.backend.trust.interfaces.rest.resources;

/**
 * Resource representing trust score information.
 */
public record TrustScoreResource(
    Float trustScore,
    Long totalReviews,
    Long recentReviews,
    Double averageRating,
    boolean hasMinimumReviews
) {}

