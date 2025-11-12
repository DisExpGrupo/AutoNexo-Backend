package com.atg.autonexo.backend.payment.domain.model.valueobjects;

/**
 * Enum representing the type of subscription payment.
 */
public enum SubscriptionPaymentType {
    /**
     * Payment for a new subscription
     */
    NEW_SUBSCRIPTION,
    
    /**
     * Payment for subscription renewal
     */
    RENEWAL,
    
    /**
     * Payment for upgrading subscription tier
     */
    UPGRADE,
    
    /**
     * Payment for downgrading subscription tier
     */
    DOWNGRADE
}

