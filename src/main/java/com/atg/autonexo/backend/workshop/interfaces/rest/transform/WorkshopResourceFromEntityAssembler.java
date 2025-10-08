package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.WorkshopResource;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assembler for converting Workshop entity to WorkshopResource
 */
public class WorkshopResourceFromEntityAssembler {
    
    /**
     * Converts a Workshop entity to a WorkshopResource
     * @param workshop the Workshop entity from the domain
     * @return WorkshopResource for REST response
     */
    public static WorkshopResource toResourceFromEntity(Workshop workshop) {
        String ruc = null;
        boolean rucVerified = false;
        
        if (workshop.getBusinessRegistration() != null) {
            ruc = workshop.getBusinessRegistration().ruc();
            rucVerified = workshop.getBusinessRegistration().verifiedBasic();
        }
        
        // Convert CapabilityTag enums to strings
        Set<String> capabilityTagNames = workshop.getCapabilityTags().stream()
            .map(Enum::name)
            .collect(Collectors.toSet());
        
        return new WorkshopResource(
            workshop.getId(),
            workshop.getOwnerUserId().id(),
            workshop.getName(),
            workshop.getShortDescription(),
            workshop.getLegalName(),
            ruc,
            rucVerified,
            workshop.getTrustScore(),
            workshop.isActive(),
            workshop.getDeletedAt(),
            workshop.getLogoUrl(),
            workshop.getPhotoUrls(),
            capabilityTagNames,
            workshop.getCreatedAt(),
            workshop.getUpdatedAt()
        );
    }
}

