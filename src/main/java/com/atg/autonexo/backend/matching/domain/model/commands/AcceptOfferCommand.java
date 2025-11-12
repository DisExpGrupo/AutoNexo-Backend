package com.atg.autonexo.backend.matching.domain.model.commands;

/**
 * Command for a user to accept an offer.
 */
public record AcceptOfferCommand(
    Long offerId,
    Long userId
) {
    public AcceptOfferCommand {
        if (offerId == null || offerId <= 0) {
            throw new IllegalArgumentException("OfferId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

