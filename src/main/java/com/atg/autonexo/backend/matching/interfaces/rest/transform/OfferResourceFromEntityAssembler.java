package com.atg.autonexo.backend.matching.interfaces.rest.transform;

import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.OfferResource;

/**
 * Assembler for converting Offer entities to REST resources.
 */
public class OfferResourceFromEntityAssembler {
    
    public static OfferResource toResourceFromEntity(Offer entity) {
        return new OfferResource(
            entity.getId(),
            entity.getServiceRequestId(),
            entity.getWorkshopId().id(),
            entity.getProposedPrice() != null ? entity.getProposedPrice().amount() : null,
            entity.getProposedPrice() != null ? entity.getProposedPrice().currency() : null,
            entity.getProposedDate(),
            entity.getStatus().name(),
            entity.getMessage(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
            entity.getExpiresAt(),
            entity.getAcceptedAt(),
            entity.getWithdrawnAt()
        );
    }
}

