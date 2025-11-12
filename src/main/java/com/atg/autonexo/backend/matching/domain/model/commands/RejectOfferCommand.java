package com.atg.autonexo.backend.matching.domain.model.commands;

/**
 * Command for a user to reject an offer.
 */
public record RejectOfferCommand(
    Long offerId,
    Long userId
) {
    public RejectOfferCommand {
        if (offerId == null || offerId <= 0) {
            throw new IllegalArgumentException("OfferId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

