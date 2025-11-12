package com.atg.autonexo.backend.payment.domain.model.commands;

/**
 * Command to mark a payment as failed (for testing purposes).
 */
public record FailPaymentCommand(
    Long paymentId
) {
    public FailPaymentCommand {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }
    }
}

