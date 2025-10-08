package com.atg.autonexo.backend.workshop.domain.model.queries;

/**
 * Query to get a workshop by owner user ID
 */
public record GetWorkshopByOwnerQuery(Long ownerUserId) {
    public GetWorkshopByOwnerQuery {
        if (ownerUserId == null || ownerUserId <= 0) {
            throw new IllegalArgumentException("Owner user ID cannot be null or negative.");
        }
    }
}

