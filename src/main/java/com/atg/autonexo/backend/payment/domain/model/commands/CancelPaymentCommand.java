package com.atg.autonexo.backend.payment.domain.model.commands;

/**
 * Command to cancel a pending payment.
 */
public record CancelPaymentCommand(
    Long paymentId
) {
    public CancelPaymentCommand {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }
    }
}

