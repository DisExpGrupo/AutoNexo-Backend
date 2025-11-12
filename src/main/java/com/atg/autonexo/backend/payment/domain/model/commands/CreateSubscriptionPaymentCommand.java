package com.atg.autonexo.backend.payment.domain.model.commands;

import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentMethod;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.SubscriptionPaymentType;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

/**
 * Command to create a new subscription payment.
 */
public record CreateSubscriptionPaymentCommand(
    Long workshopId,
    SubscriptionTier subscriptionTier,
    PaymentMethod paymentMethod,
    SubscriptionPaymentType paymentType,
    String description
) {
    public CreateSubscriptionPaymentCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID must be valid");
        }
        if (subscriptionTier == null) {
            throw new IllegalArgumentException("Subscription tier cannot be null");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
    }
}

