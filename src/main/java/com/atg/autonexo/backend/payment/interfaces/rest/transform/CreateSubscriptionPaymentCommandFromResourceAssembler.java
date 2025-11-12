package com.atg.autonexo.backend.payment.interfaces.rest.transform;

import com.atg.autonexo.backend.payment.domain.model.commands.CreateSubscriptionPaymentCommand;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.CreateSubscriptionPaymentResource;

/**
 * Assembler to convert CreateSubscriptionPaymentResource to CreateSubscriptionPaymentCommand.
 */
public class CreateSubscriptionPaymentCommandFromResourceAssembler {
    
    public static CreateSubscriptionPaymentCommand toCommandFromResource(CreateSubscriptionPaymentResource resource) {
        return new CreateSubscriptionPaymentCommand(
            resource.workshopId(),
            resource.subscriptionTier(),
            resource.paymentMethod(),
            resource.paymentType(),
            resource.description()
        );
    }
}

