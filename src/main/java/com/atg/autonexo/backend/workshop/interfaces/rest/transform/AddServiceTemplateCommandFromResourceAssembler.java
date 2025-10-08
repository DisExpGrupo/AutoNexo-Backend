package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.commands.AddServiceTemplateCommand;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.AddServiceTemplateResource;

/**
 * Assembler for converting AddServiceTemplateResource to AddServiceTemplateCommand
 */
public class AddServiceTemplateCommandFromResourceAssembler {
    
    /**
     * Converts an AddServiceTemplateResource to an AddServiceTemplateCommand
     * @param workshopId the workshop ID from context
     * @param resource the AddServiceTemplateResource from the REST request
     * @return AddServiceTemplateCommand for domain processing
     */
    public static AddServiceTemplateCommand toCommandFromResource(Long workshopId, AddServiceTemplateResource resource) {
        return new AddServiceTemplateCommand(
            workshopId,
            resource.code(),
            resource.catalogService(),      // Optional - can be null for custom services
            resource.customName(),           // Required - workshop's name for the service
            resource.description(),
            resource.estimatedDurationMinutes(),
            resource.basePriceAmount(),
            resource.currency()
        );
    }
}
