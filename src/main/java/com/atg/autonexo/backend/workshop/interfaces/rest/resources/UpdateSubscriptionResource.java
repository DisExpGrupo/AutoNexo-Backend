package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Resource for updating workshop subscription.
 */
public record UpdateSubscriptionResource(
    @NotNull(message = "Subscription status is required")
    SubscriptionStatus status,
    
    @NotNull(message = "Subscription tier is required")
    SubscriptionTier tier,
    
    LocalDateTime expiresAt
) {}


