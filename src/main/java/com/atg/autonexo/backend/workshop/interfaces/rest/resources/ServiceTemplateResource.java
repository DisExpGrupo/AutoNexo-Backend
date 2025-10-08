package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Resource for service template information responses.
 * Represents a workshop's service offering.
 */
public record ServiceTemplateResource(
    Long id,
    String code,
    
    /**
     * Link to ServiceCatalog (enum name), null if custom service
     */
    String catalogService,
    
    /**
     * Service category (from catalog if linked, null otherwise)
     */
    String serviceCategory,
    
    /**
     * Workshop's custom name for this service
     */
    String customName,
    
    /**
     * Display name (combines catalog + custom name if linked)
     */
    String displayName,
    
    String description,
    Integer estimatedDurationMinutes,
    BigDecimal basePriceAmount,
    String currency,
    boolean active,
    
    /**
     * Indicates if this service is linked to the catalog
     */
    boolean linkedToCatalog,
    
    Date createdAt,
    Date updatedAt
) {
}
