package com.atg.autonexo.backend.workshop.domain.services;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetInvitationByCodeQuery;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetInvitationsByWorkshopQuery;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for Invitation query operations.
 */
public interface InvitationQueryService {
    
    /**
     * Gets an invitation by code
     */
    Optional<Invitation> handle(GetInvitationByCodeQuery query);
    
    /**
     * Gets all invitations for a workshop
     */
    List<Invitation> handle(GetInvitationsByWorkshopQuery query);
}

