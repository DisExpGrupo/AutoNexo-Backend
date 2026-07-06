package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Resource for offer representation.
 */
public record OfferResource(
    Long id,
    Long serviceRequestId,
    Long workshopId,
    Float workshopTrustScore,
    BigDecimal proposedPriceAmount,
    String currency,
    LocalDateTime proposedDate,
    String status,
    String message,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    LocalDateTime acceptedAt,
    LocalDateTime withdrawnAt
) {}

