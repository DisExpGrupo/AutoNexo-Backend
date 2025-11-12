package com.atg.autonexo.backend.payment.domain.model.commands;

/**
 * Command to refund a completed payment.
 */
public record RefundPaymentCommand(
    Long paymentId,
    String reason
) {
    public RefundPaymentCommand {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }
    }
}

