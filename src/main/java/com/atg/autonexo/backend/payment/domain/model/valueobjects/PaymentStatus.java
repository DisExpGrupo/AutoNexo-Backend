package com.atg.autonexo.backend.payment.domain.model.valueobjects;

/**
 * Enum representing the status of a payment.
 */
public enum PaymentStatus {
    /**
     * Payment is pending processing
     */
    PENDING,
    
    /**
     * Payment completed successfully
     */
    COMPLETED,
    
    /**
     * Payment failed
     */
    FAILED,
    
    /**
     * Payment was refunded
     */
    REFUNDED,
    
    /**
     * Payment was cancelled before processing
     */
    CANCELLED
}

