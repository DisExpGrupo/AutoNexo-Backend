package com.atg.autonexo.backend.payment.domain.model.aggregates;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.atg.autonexo.backend.payment.domain.exceptions.InvalidPaymentStatusException;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentMethod;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentStatus;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.SubscriptionPaymentType;
import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * Payment aggregate root.
 * Represents a subscription payment made by a workshop.
 */
@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "payments")
public class Payment extends AuditableAbstractAggregateRoot<Payment> {
    
    @Embedded
    private WorkshopId workshopId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionTier subscriptionTier;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount_value")),
        @AttributeOverride(name = "currency", column = @Column(name = "amount_currency"))
    })
    private Money amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionPaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;
    
    @Column
    private LocalDateTime paymentDate;
    
    @Column(length = 100)
    private String transactionId;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private LocalDate billingPeriodStart;
    
    @Column(nullable = false)
    private LocalDate billingPeriodEnd;
    
    @Column
    private LocalDate nextBillingDate;
    
    @Column(length = 500)
    private String invoiceUrl;
    
    protected Payment() {}
    
    /**
     * Creates a new payment for a workshop subscription.
     */
    public Payment(WorkshopId workshopId, SubscriptionTier subscriptionTier, Money amount,
                   PaymentMethod paymentMethod, SubscriptionPaymentType paymentType,
                   LocalDate billingPeriodStart, LocalDate billingPeriodEnd, String description) {
        if (workshopId == null) {
            throw new IllegalArgumentException("Workshop ID cannot be null");
        }
        if (subscriptionTier == null) {
            throw new IllegalArgumentException("Subscription tier cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        if (billingPeriodStart == null || billingPeriodEnd == null) {
            throw new IllegalArgumentException("Billing period dates cannot be null");
        }
        if (billingPeriodEnd.isBefore(billingPeriodStart)) {
            throw new IllegalArgumentException("Billing period end must be after start");
        }
        
        this.workshopId = workshopId;
        this.subscriptionTier = subscriptionTier;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.status = PaymentStatus.PENDING;
        this.billingPeriodStart = billingPeriodStart;
        this.billingPeriodEnd = billingPeriodEnd;
        this.description = description;
        this.transactionId = generateTransactionId();
        
        // Calculate next billing date (typically end of current period)
        this.nextBillingDate = billingPeriodEnd;
    }
    
    /**
     * Complete the payment.
     * Marks the payment as completed and sets the payment date.
     */
    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException(this.status, PaymentStatus.COMPLETED);
        }
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
    }
    
    /**
     * Mark the payment as failed.
     */
    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException(this.status, PaymentStatus.FAILED);
        }
        this.status = PaymentStatus.FAILED;
        this.paymentDate = LocalDateTime.now();
    }
    
    /**
     * Refund the payment.
     * Can only refund a completed payment.
     */
    public void refund() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new InvalidPaymentStatusException(this.status, PaymentStatus.REFUNDED);
        }
        this.status = PaymentStatus.REFUNDED;
    }
    
    /**
     * Cancel the payment.
     * Can only cancel a pending payment.
     */
    public void cancel() {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException(this.status, PaymentStatus.CANCELLED);
        }
        this.status = PaymentStatus.CANCELLED;
    }
    
    /**
     * Check if payment is completed.
     */
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Check if payment is pending.
     */
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }
    
    /**
     * Check if payment can be refunded.
     */
    public boolean canBeRefunded() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Generate a unique transaction ID.
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }
}

