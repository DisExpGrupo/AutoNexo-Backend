package com.atg.autonexo.backend.matching.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Command for a workshop to withdraw an offer.
 */
public record WithdrawOfferCommand(
    Long offerId,
    WorkshopId workshopId
) {
    public WithdrawOfferCommand {
        if (offerId == null || offerId <= 0) {
            throw new IllegalArgumentException("OfferId must be valid");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
    }
}

