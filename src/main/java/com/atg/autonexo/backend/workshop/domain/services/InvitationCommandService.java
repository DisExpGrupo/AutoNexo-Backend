package com.atg.autonexo.backend.workshop.domain.services;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.commands.AcceptInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.CreateInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;

/**
 * Domain service interface for Invitation command operations.
 */
public interface InvitationCommandService {
    
    /**
     * Creates and sends a new invitation
     */
    Invitation handle(CreateInvitationCommand command);
    
    /**
     * Accepts an invitation and creates a staff member
     */
    StaffMember handle(AcceptInvitationCommand command);
}

