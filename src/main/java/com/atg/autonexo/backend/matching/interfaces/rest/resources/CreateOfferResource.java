package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating an offer.
 */
public record CreateOfferResource(
    @NotNull(message = "Service request ID is required")
    Long serviceRequestId,
    
    @NotNull(message = "Proposed price amount is required")
    BigDecimal proposedPriceAmount,
    
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    String currency,
    
    LocalDateTime proposedDate,
    
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    String message
) {}

