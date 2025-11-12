package com.atg.autonexo.backend.payment.application.internal.commandservices;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.payment.domain.exceptions.PaymentNotFoundException;
import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.domain.model.commands.CancelPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.CompletePaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.CreateSubscriptionPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.FailPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.RefundPaymentCommand;
import com.atg.autonexo.backend.payment.domain.services.PaymentCommandService;
import com.atg.autonexo.backend.payment.infrastructure.persistence.jpa.PaymentRepository;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.application.acl.WorkshopContextFacadeImpl;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of PaymentCommandService.
 */
@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl implements PaymentCommandService {
    
    private final PaymentRepository paymentRepository;
    private final WorkshopContextFacadeImpl workshopFacade;
    
    @Override
    @Transactional
    public Payment handle(CreateSubscriptionPaymentCommand command) {
        // Calculate amount based on subscription tier
        Money amount = calculateSubscriptionPrice(command.subscriptionTier());
        
        // Calculate billing period (1 month)
        LocalDate billingStart = LocalDate.now();
        LocalDate billingEnd = billingStart.plusMonths(1);
        
        // Create payment
        Payment payment = new Payment(
            new WorkshopId(command.workshopId()),
            command.subscriptionTier(),
            amount,
            command.paymentMethod(),
            command.paymentType(),
            billingStart,
            billingEnd,
            command.description()
        );
        
        // Save payment
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public Payment handle(CompletePaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
            .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));
        
        // Complete payment
        payment.complete();
        payment = paymentRepository.save(payment);
        
        // Update workshop subscription
        updateWorkshopSubscription(payment);
        
        return payment;
    }
    
    @Override
    @Transactional
    public Payment handle(FailPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
            .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));
        
        payment.fail();
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public Payment handle(RefundPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
            .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));
        
        payment.refund();
        payment = paymentRepository.save(payment);
        
        // Optionally downgrade or cancel workshop subscription
        // For now, we'll just log it
        // workshopFacade.downgradeSubscription(payment.getWorkshopId().id());
        
        return payment;
    }
    
    @Override
    @Transactional
    public Payment handle(CancelPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
            .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));
        
        payment.cancel();
        return paymentRepository.save(payment);
    }
    
    /**
     * Calculate subscription price based on tier.
     */
    private Money calculateSubscriptionPrice(SubscriptionTier tier) {
        BigDecimal amount = switch (tier) {
            case FREE -> BigDecimal.ZERO;
            case BASIC -> new BigDecimal("19.99");
            case PREMIUM -> new BigDecimal("49.99");
        };
        
        return new Money(amount, "USD");
    }
    
    /**
     * Update workshop subscription after successful payment.
     */
    private void updateWorkshopSubscription(Payment payment) {
        // Update workshop subscription tier and status
        workshopFacade.updateSubscription(
            payment.getWorkshopId().id(),
            payment.getSubscriptionTier(),
            SubscriptionStatus.ACTIVE,
            payment.getBillingPeriodEnd()
        );
    }
}

