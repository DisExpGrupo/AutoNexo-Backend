package com.atg.autonexo.backend.matching.interfaces.rest.transform;

import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.OfferResource;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetWorkshopByIdQuery;
import com.atg.autonexo.backend.workshop.domain.services.WorkshopQueryService;

/**
 * Assembler for converting Offer entities to REST resources.
 */
public class OfferResourceFromEntityAssembler {

    public static OfferResource toResourceFromEntity(Offer entity, WorkshopQueryService workshopQueryService) {
        Float trustScore = workshopQueryService
            .handle(new GetWorkshopByIdQuery(entity.getWorkshopId().id()))
            .map(w -> w.getTrustScore())
            .orElse(null);

        return new OfferResource(
            entity.getId(),
            entity.getServiceRequestId(),
            entity.getWorkshopId().id(),
            trustScore,
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

