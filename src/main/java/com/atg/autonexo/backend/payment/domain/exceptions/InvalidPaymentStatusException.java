package com.atg.autonexo.backend.payment.domain.exceptions;

import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentStatus;

/**
 * Exception thrown when attempting an invalid payment status transition.
 */
public class InvalidPaymentStatusException extends RuntimeException {
    public InvalidPaymentStatusException(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        super(String.format("Cannot transition from %s to %s", currentStatus, targetStatus));
    }
}

