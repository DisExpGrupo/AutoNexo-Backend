package com.atg.autonexo.backend.workshop.domain.model.queries;

/**
 * Query to get a workshop by ID
 */
public record GetWorkshopByIdQuery(Long workshopId) {
    public GetWorkshopByIdQuery {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
    }
}

