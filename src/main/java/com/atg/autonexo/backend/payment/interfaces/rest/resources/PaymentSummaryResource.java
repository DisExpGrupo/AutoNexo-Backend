package com.atg.autonexo.backend.payment.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

/**
 * REST resource for a summarized payment view.
 */
public record PaymentSummaryResource(
    Long id,
    SubscriptionTier subscriptionTier,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    LocalDateTime paymentDate,
    String transactionId
) {}

