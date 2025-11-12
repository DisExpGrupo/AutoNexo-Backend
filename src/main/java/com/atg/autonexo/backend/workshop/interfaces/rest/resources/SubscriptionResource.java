package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import java.time.LocalDateTime;

/**
 * Resource representing workshop subscription information.
 */
public record SubscriptionResource(
    SubscriptionStatus status,
    SubscriptionTier tier,
    LocalDateTime expiresAt,
    boolean isActive,
    boolean canAccessPremiumFeatures
) {}


