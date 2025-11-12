package com.atg.autonexo.backend.matching.domain.model.queries;

/**
 * Query to get all offers for a service request.
 */
public record GetOffersByServiceRequestQuery(
    Long serviceRequestId
) {
    public GetOffersByServiceRequestQuery {
        if (serviceRequestId == null || serviceRequestId <= 0) {
            throw new IllegalArgumentException("ServiceRequestId must be valid");
        }
    }
}

