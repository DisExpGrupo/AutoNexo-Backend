package com.atg.autonexo.backend.workshop.domain.model.queries;

/**
 * Query to get all invitations for a workshop
 */
public record GetInvitationsByWorkshopQuery(Long workshopId) {
    public GetInvitationsByWorkshopQuery {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
    }
}

