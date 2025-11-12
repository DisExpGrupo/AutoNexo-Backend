package com.atg.autonexo.backend.payment.domain.model.valueobjects;

/**
 * Enum representing payment methods for subscription payments.
 */
public enum PaymentMethod {
    /**
     * Credit card payment
     */
    CREDIT_CARD,
    
    /**
     * Debit card payment
     */
    DEBIT_CARD,
    
    /**
     * Bank transfer
     */
    BANK_TRANSFER,
    
    /**
     * Digital wallet (Yape, Plin, etc.)
     */
    DIGITAL_WALLET
}

