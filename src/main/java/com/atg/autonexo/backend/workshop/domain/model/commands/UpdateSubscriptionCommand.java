package com.atg.autonexo.backend.workshop.domain.model.commands;

import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import java.time.LocalDateTime;

/**
 * Command to update workshop subscription.
 */
public record UpdateSubscriptionCommand(
    Long workshopId,
    SubscriptionStatus status,
    SubscriptionTier tier,
    LocalDateTime expiresAt
) {
    public UpdateSubscriptionCommand {
        if (workshopId == null) {
            throw new IllegalArgumentException("Workshop ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Subscription status cannot be null");
        }
        if (tier == null) {
            throw new IllegalArgumentException("Subscription tier cannot be null");
        }
    }
}


