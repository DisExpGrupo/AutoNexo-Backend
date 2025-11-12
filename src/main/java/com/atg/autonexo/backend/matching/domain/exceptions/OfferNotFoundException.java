package com.atg.autonexo.backend.matching.domain.exceptions;

/**
 * Exception thrown when an offer is not found.
 */
public class OfferNotFoundException extends RuntimeException {
    
    public OfferNotFoundException(Long offerId) {
        super("Offer not found with ID: " + offerId);
    }
    
    public OfferNotFoundException(String message) {
        super(message);
    }
}

