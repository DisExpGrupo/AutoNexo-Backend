package com.atg.autonexo.backend.payment.interfaces.rest.resources;

import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentMethod;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.SubscriptionPaymentType;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

/**
 * REST resource for creating a subscription payment.
 */
public record CreateSubscriptionPaymentResource(
    Long workshopId,
    SubscriptionTier subscriptionTier,
    PaymentMethod paymentMethod,
    SubscriptionPaymentType paymentType,
    String description
) {}

