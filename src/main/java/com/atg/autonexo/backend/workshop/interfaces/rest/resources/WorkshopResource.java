package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Resource for workshop information responses
 */
public record WorkshopResource(
    Long id,
    Long ownerUserId,
    String name,
    String shortDescription,
    String legalName,
    String ruc,
    boolean rucVerified,
    Float trustScore,
    boolean active,
    LocalDateTime deletedAt,
    String logoUrl,
    List<String> photoUrls,
    
    /**
     * Capability tags (enum names: LIGHT_VEHICLES, TOYOTA, DIESEL_SPECIALIST, etc.)
     */
    Set<String> capabilityTags,
    
    Date createdAt,
    Date updatedAt
) {
}

