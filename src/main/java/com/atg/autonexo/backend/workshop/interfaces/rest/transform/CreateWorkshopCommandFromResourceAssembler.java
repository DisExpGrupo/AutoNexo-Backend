package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.commands.CreateWorkshopCommand;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.CreateWorkshopResource;

/**
 * Assembler for converting CreateWorkshopResource to CreateWorkshopCommand
 */
public class CreateWorkshopCommandFromResourceAssembler {
    
    /**
     * Converts a CreateWorkshopResource to a CreateWorkshopCommand
     * @param resource the CreateWorkshopResource from the REST request
     * @return CreateWorkshopCommand for domain processing
     */
    public static CreateWorkshopCommand toCommandFromResource(CreateWorkshopResource resource) {
        return new CreateWorkshopCommand(
            resource.ownerUserId(),
            resource.name(),
            resource.shortDescription(),
            resource.legalName(),
            resource.ruc()
        );
    }
}

