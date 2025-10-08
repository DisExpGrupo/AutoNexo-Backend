package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.InvitationResource;

/**
 * Assembler for converting Invitation entity to InvitationResource
 */
public class InvitationResourceFromEntityAssembler {
    
    /**
     * Converts an Invitation entity to an InvitationResource
     * @param invitation the Invitation entity from the domain
     * @return InvitationResource for REST response
     */
    public static InvitationResource toResourceFromEntity(Invitation invitation) {
        String email = invitation.getDeliveredTo() != null ? 
                      invitation.getDeliveredTo().value() : null;
        
        return new InvitationResource(
            invitation.getId(),
            invitation.getInvitationCode().value(),
            email,
            invitation.getWorkshopId().id(),
            invitation.getMessage(),
            invitation.getExpiresAt(),
            invitation.isUsed(),
            invitation.isExpired(),
            invitation.canBeUsed(),
            invitation.getCreatedAt()
        );
    }
}

