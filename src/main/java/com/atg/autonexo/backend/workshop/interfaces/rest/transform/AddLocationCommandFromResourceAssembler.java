package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.commands.AddLocationCommand;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.AddLocationResource;

/**
 * Assembler for converting AddLocationResource to AddLocationCommand
 */
public class AddLocationCommandFromResourceAssembler {
    
    /**
     * Converts an AddLocationResource to an AddLocationCommand
     * @param workshopId the workshop ID from the path variable
     * @param resource the AddLocationResource from the REST request
     * @return AddLocationCommand for domain processing
     */
    public static AddLocationCommand toCommandFromResource(Long workshopId, AddLocationResource resource) {
        return new AddLocationCommand(
            workshopId,
            resource.street(),
            resource.city(),
            resource.state(),
            resource.zip(),
            resource.country(),
            resource.latitude(),
            resource.longitude()
        );
    }
}

