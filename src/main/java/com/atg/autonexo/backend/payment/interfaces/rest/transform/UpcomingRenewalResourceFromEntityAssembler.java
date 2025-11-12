package com.atg.autonexo.backend.payment.interfaces.rest.transform;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.UpcomingRenewalResource;

/**
 * Assembler to convert Payment entity to UpcomingRenewalResource.
 */
public class UpcomingRenewalResourceFromEntityAssembler {
    
    public static UpcomingRenewalResource toResourceFromEntity(Payment payment) {
        LocalDate today = LocalDate.now();
        int daysUntilRenewal = payment.getNextBillingDate() != null 
            ? (int) ChronoUnit.DAYS.between(today, payment.getNextBillingDate())
            : 0;
        
        return new UpcomingRenewalResource(
            payment.getWorkshopId().id(),
            payment.getSubscriptionTier(),
            payment.getAmount().amount(),
            payment.getAmount().currency(),
            payment.getNextBillingDate(),
            daysUntilRenewal
        );
    }
}

