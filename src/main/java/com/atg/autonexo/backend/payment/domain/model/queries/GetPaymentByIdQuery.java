package com.atg.autonexo.backend.payment.domain.model.queries;

/**
 * Query to get a payment by its ID.
 */
public record GetPaymentByIdQuery(
    Long paymentId
) {
    public GetPaymentByIdQuery {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }
    }
}

