package com.atg.autonexo.backend.matching.domain.model.queries;

/**
 * Query to get a service request by ID.
 */
public record GetServiceRequestByIdQuery(
    Long serviceRequestId
) {
    public GetServiceRequestByIdQuery {
        if (serviceRequestId == null || serviceRequestId <= 0) {
            throw new IllegalArgumentException("ServiceRequestId must be valid");
        }
    }
}

