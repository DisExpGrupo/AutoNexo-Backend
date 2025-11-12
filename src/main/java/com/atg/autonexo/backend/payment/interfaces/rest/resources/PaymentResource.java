package com.atg.autonexo.backend.payment.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentMethod;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentStatus;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.SubscriptionPaymentType;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

/**
 * REST resource representing a payment.
 */
public record PaymentResource(
    Long id,
    Long workshopId,
    SubscriptionTier subscriptionTier,
    BigDecimal amount,
    String currency,
    PaymentMethod paymentMethod,
    SubscriptionPaymentType paymentType,
    PaymentStatus status,
    LocalDateTime paymentDate,
    String transactionId,
    String description,
    LocalDate billingPeriodStart,
    LocalDate billingPeriodEnd,
    LocalDate nextBillingDate,
    String invoiceUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

