package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.commands.UpdateWorkshopCommand;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.UpdateWorkshopResource;

/**
 * Assembler for converting UpdateWorkshopResource to UpdateWorkshopCommand
 */
public class UpdateWorkshopCommandFromResourceAssembler {
    
    /**
     * Converts an UpdateWorkshopResource to an UpdateWorkshopCommand
     * @param workshopId the workshop ID from the path variable
     * @param resource the UpdateWorkshopResource from the REST request
     * @return UpdateWorkshopCommand for domain processing
     */
    public static UpdateWorkshopCommand toCommandFromResource(Long workshopId, UpdateWorkshopResource resource) {
        return new UpdateWorkshopCommand(
            workshopId,
            resource.name(),
            resource.shortDescription(),
            resource.legalName()
        );
    }
}

