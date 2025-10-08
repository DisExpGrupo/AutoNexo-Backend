package com.atg.autonexo.backend.workshop.domain.model.entities;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.ServiceTemplateCode;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * ServiceTemplate entity representing a workshop's service offering.
 * Can be linked to a known ServiceCatalog entry or be a custom service.
 * Part of the Workshop aggregate.
 */
@Entity
@Getter
@Setter
public class ServiceTemplate extends AuditableModel {
    
    @Embedded
    private ServiceTemplateCode code;
    
    /**
     * Optional link to the service catalog.
     * Null for custom services not in the catalog.
     */
    @Column(name = "catalog_service", length = 100)
    private ServiceCatalog catalogService;
    
    /**
     * Custom name given by the workshop.
     * Required field - workshop must provide their own name.
     */
    @Column(nullable = false, length = 200)
    private String customName;
    
    /**
     * Custom description provided by the workshop.
     */
    @Column(length = 1000)
    private String description;
    
    /**
     * Estimated duration in minutes
     */
    @Column(nullable = false)
    private Integer estimatedDurationMinutes;
    
    /**
     * Base price for this service
     */
    @Embedded
    private Money basePrice;
    
    @Column(nullable = false)
    private boolean active = true;
    
    protected ServiceTemplate() {}
    
    /**
     * Constructor for a service template
     * @param code Service template code
     * @param catalogService Optional link to catalog (null for custom services)
     * @param customName Workshop's name for the service (required)
     * @param description Workshop's description
     * @param estimatedDurationMinutes Estimated duration
     * @param basePrice Base price
     */
    public ServiceTemplate(ServiceTemplateCode code, ServiceCatalog catalogService, 
                           String customName, String description, 
                           Integer estimatedDurationMinutes, Money basePrice) {
        if (customName == null || customName.isBlank()) {
            throw new IllegalArgumentException("Custom name cannot be null or blank");
        }
        if (estimatedDurationMinutes == null || estimatedDurationMinutes <= 0) {
            throw new IllegalArgumentException("Estimated duration must be positive");
        }
        
        this.code = code;
        this.catalogService = catalogService;
        this.customName = customName;
        this.description = description;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.basePrice = basePrice;
        this.active = true;
    }
    
    /**
     * Checks if this service is linked to the catalog
     */
    public boolean isLinkedToCatalog() {
        return catalogService != null;
    }
    
    /**
     * Checks if this is a custom service (not in catalog)
     */
    public boolean isCustomService() {
        return catalogService == null;
    }
    
    /**
     * Updates the base price
     */
    public void updateBasePrice(Money newPrice) {
        this.basePrice = newPrice;
    }
    
    /**
     * Deactivates this service template
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Activates this service template
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Updates service details
     */
    public void updateDetails(String customName, String description, Integer estimatedDurationMinutes) {
        if (customName != null && !customName.isBlank()) {
            this.customName = customName;
        }
        if (description != null) {
            this.description = description;
        }
        if (estimatedDurationMinutes != null && estimatedDurationMinutes > 0) {
            this.estimatedDurationMinutes = estimatedDurationMinutes;
        }
    }
    
    /**
     * Gets the display name (catalog name if linked, custom name otherwise)
     */
    public String getDisplayName() {
        if (catalogService != null) {
            return String.format("%s - %s", catalogService.getDisplayName(), customName);
        }
        return customName;
    }
    
    /**
     * Gets the service category (from catalog if linked, null otherwise)
     */
    public com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCategory getCategory() {
        return catalogService != null ? catalogService.getCategory() : null;
    }
}

