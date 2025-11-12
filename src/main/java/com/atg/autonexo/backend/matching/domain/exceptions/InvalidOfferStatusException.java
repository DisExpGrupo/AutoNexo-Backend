package com.atg.autonexo.backend.matching.domain.exceptions;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus;

/**
 * Exception thrown when an operation is attempted on an offer with an invalid status.
 */
public class InvalidOfferStatusException extends RuntimeException {
    
    public InvalidOfferStatusException(Long offerId, OfferStatus currentStatus, String operation) {
        super(String.format("Cannot %s offer %d: current status is %s", operation, offerId, currentStatus));
    }
    
    public InvalidOfferStatusException(String message) {
        super(message);
    }
}

