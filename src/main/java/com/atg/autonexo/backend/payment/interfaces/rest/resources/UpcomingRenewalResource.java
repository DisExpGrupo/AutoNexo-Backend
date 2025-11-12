package com.atg.autonexo.backend.payment.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

/**
 * REST resource for upcoming subscription renewal information.
 */
public record UpcomingRenewalResource(
    Long workshopId,
    SubscriptionTier subscriptionTier,
    BigDecimal amount,
    String currency,
    LocalDate nextBillingDate,
    int daysUntilRenewal
) {}

