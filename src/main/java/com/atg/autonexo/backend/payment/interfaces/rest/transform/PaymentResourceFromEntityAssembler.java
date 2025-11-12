package com.atg.autonexo.backend.payment.interfaces.rest.transform;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.PaymentResource;

/**
 * Assembler to convert Payment entity to PaymentResource.
 */
public class PaymentResourceFromEntityAssembler {
    
    public static PaymentResource toResourceFromEntity(Payment payment) {
        return new PaymentResource(
            payment.getId(),
            payment.getWorkshopId().id(),
            payment.getSubscriptionTier(),
            payment.getAmount().amount(),
            payment.getAmount().currency(),
            payment.getPaymentMethod(),
            payment.getPaymentType(),
            payment.getStatus(),
            payment.getPaymentDate(),
            payment.getTransactionId(),
            payment.getDescription(),
            payment.getBillingPeriodStart(),
            payment.getBillingPeriodEnd(),
            payment.getNextBillingDate(),
            payment.getInvoiceUrl(),
            LocalDateTime.ofInstant(payment.getCreatedAt().toInstant(), ZoneId.systemDefault()),
            LocalDateTime.ofInstant(payment.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
    }
}

