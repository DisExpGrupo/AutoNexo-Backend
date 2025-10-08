package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.ServiceTemplateResource;

import java.math.BigDecimal;

/**
 * Assembler for converting ServiceTemplate entity to ServiceTemplateResource
 */
public class ServiceTemplateResourceFromEntityAssembler {
    
    /**
     * Converts a ServiceTemplate entity to a ServiceTemplateResource
     * @param template the ServiceTemplate entity from the domain
     * @return ServiceTemplateResource for REST response
     */
    public static ServiceTemplateResource toResourceFromEntity(ServiceTemplate template) {
        String code = template.getCode() != null ? template.getCode().value() : null;
        BigDecimal basePriceAmount = null;
        String currency = null;
        
        if (template.getBasePrice() != null) {
            basePriceAmount = template.getBasePrice().amount();
            currency = template.getBasePrice().currency();
        }
        
        // Convert ServiceCatalog enum to string (null if custom service)
        String catalogService = template.getCatalogService() != null 
            ? template.getCatalogService().name() 
            : null;
        
        // Get service category name (from catalog if linked)
        String serviceCategory = template.getCategory() != null 
            ? template.getCategory().name() 
            : null;
        
        return new ServiceTemplateResource(
            template.getId(),
            code,
            catalogService,
            serviceCategory,
            template.getCustomName(),
            template.getDisplayName(),
            template.getDescription(),
            template.getEstimatedDurationMinutes(),
            basePriceAmount,
            currency,
            template.isActive(),
            template.isLinkedToCatalog(),
            template.getCreatedAt(),
            template.getUpdatedAt()
        );
    }
}
