package com.atg.autonexo.backend.payment.domain.model.commands;

/**
 * Command to complete a pending payment (simulated payment processing).
 */
public record CompletePaymentCommand(
    Long paymentId
) {
    public CompletePaymentCommand {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }
    }
}

