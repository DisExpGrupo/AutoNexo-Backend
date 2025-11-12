package com.atg.autonexo.backend.payment.domain.services;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.domain.model.commands.CancelPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.CompletePaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.CreateSubscriptionPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.FailPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.RefundPaymentCommand;

/**
 * Service interface for Payment command operations.
 */
public interface PaymentCommandService {
    
    /**
     * Handle create subscription payment command.
     */
    Payment handle(CreateSubscriptionPaymentCommand command);
    
    /**
     * Handle complete payment command.
     */
    Payment handle(CompletePaymentCommand command);
    
    /**
     * Handle fail payment command.
     */
    Payment handle(FailPaymentCommand command);
    
    /**
     * Handle refund payment command.
     */
    Payment handle(RefundPaymentCommand command);
    
    /**
     * Handle cancel payment command.
     */
    Payment handle(CancelPaymentCommand command);
}

