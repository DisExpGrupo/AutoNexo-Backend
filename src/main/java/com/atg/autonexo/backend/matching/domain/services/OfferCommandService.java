package com.atg.autonexo.backend.matching.domain.services;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceBooking;
import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.domain.model.commands.AcceptOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.CreateOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.RejectOfferCommand;
import com.atg.autonexo.backend.matching.domain.model.commands.WithdrawOfferCommand;

/**
 * Domain service interface for Offer command operations.
 */
public interface OfferCommandService {
    
    Offer handle(CreateOfferCommand command);
    
    void handle(WithdrawOfferCommand command);
    
    ServiceBooking handle(AcceptOfferCommand command);
    
    void handle(RejectOfferCommand command);
}

