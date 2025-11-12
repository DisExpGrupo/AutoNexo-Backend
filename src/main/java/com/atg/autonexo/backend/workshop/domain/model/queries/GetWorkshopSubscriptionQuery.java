package com.atg.autonexo.backend.workshop.domain.model.queries;

/**
 * Query to get workshop subscription information.
 */
public record GetWorkshopSubscriptionQuery(Long workshopId) {
    public GetWorkshopSubscriptionQuery {
        if (workshopId == null) {
            throw new IllegalArgumentException("Workshop ID cannot be null");
        }
    }
}


