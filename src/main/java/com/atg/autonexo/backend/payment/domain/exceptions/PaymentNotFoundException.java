package com.atg.autonexo.backend.payment.domain.exceptions;

/**
 * Exception thrown when a payment is not found.
 */
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long paymentId) {
        super(String.format("Payment with id %d not found", paymentId));
    }
}

