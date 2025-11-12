package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Resource for service booking representation.
 */
public record ServiceBookingResource(
    Long id,
    Long serviceRequestId,
    Long offerId,
    Long userId,
    Long vehicleId,
    Long workshopId,
    LocalDateTime scheduledDate,
    BigDecimal proposedPriceAmount,
    String proposedPriceCurrency,
    BigDecimal finalPriceAmount,
    String finalPriceCurrency,
    String status,
    List<String> servicesToPerform,
    String description,
    LocalDateTime createdAt,
    LocalDateTime completedAt,
    LocalDateTime pickedUpAt,
    LocalDateTime cancelledAt,
    Long cancelledBy,
    String cancellationReason
) {}

