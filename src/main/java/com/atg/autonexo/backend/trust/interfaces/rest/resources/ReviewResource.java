package com.atg.autonexo.backend.trust.interfaces.rest.resources;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewStatus;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewType;

/**
 * Resource representing a review.
 */
public record ReviewResource(
    Long id,
    Long serviceBookingId,
    Long reviewerId,
    Long revieweeUserId,
    Long revieweeWorkshopId,
    ReviewType reviewType,
    Integer rating,
    String comment,
    ReviewStatus status,
    LocalDateTime submittedAt,
    LocalDateTime windowExpiresAt,
    Long reportsCount,
    LocalDateTime createdAt
) {}

