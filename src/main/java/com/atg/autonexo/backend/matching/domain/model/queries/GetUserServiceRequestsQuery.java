package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceRequestStatus;

/**
 * Query to get service requests for a user.
 */
public record GetUserServiceRequestsQuery(
    Long userId,
    ServiceRequestStatus status
) {
    public GetUserServiceRequestsQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
        // status can be null to get all requests
    }
}

