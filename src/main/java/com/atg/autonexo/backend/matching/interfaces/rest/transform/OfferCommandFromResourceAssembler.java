package com.atg.autonexo.backend.matching.interfaces.rest.transform;

import com.atg.autonexo.backend.matching.domain.model.commands.AcceptOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.CreateOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.RejectOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.WithdrawOfferCommand;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.CreateOfferResource;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Assembler for converting REST resources to Offer commands.
 */
public class OfferCommandFromResourceAssembler {
    
    public static CreateOfferCommand toCommandFromResource(CreateOfferResource resource, Long workshopId) {
        Money proposedPrice = new Money(resource.proposedPriceAmount(), resource.currency());
        
        return new CreateOfferCommand(
            resource.serviceRequestId(),
            new WorkshopId(workshopId),
            proposedPrice,
            resource.proposedDate(),
            resource.message()
        );
    }
    
    public static AcceptOfferCommand toAcceptCommand(Long offerId, Long userId) {
        return new AcceptOfferCommand(offerId, userId);
    }
    
    public static RejectOfferCommand toRejectCommand(Long offerId, Long userId) {
        return new RejectOfferCommand(offerId, userId);
    }
    
    public static WithdrawOfferCommand toWithdrawCommand(Long offerId, Long workshopId) {
        return new WithdrawOfferCommand(offerId, new WorkshopId(workshopId));
    }
}

