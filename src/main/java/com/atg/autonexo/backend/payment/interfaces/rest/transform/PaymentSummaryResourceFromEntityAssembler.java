package com.atg.autonexo.backend.payment.interfaces.rest.transform;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.PaymentSummaryResource;

/**
 * Assembler to convert Payment entity to PaymentSummaryResource.
 */
public class PaymentSummaryResourceFromEntityAssembler {
    
    public static PaymentSummaryResource toResourceFromEntity(Payment payment) {
        return new PaymentSummaryResource(
            payment.getId(),
            payment.getSubscriptionTier(),
            payment.getAmount().amount(),
            payment.getAmount().currency(),
            payment.getStatus(),
            payment.getPaymentDate(),
            payment.getTransactionId()
        );
    }
}

