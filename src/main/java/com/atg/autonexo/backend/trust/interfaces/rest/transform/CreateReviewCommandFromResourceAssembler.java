package com.atg.autonexo.backend.trust.interfaces.rest.transform;

import com.atg.autonexo.backend.trust.domain.model.commands.CreateReviewCommand;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.CreateReviewResource;

/**
 * Assembler to convert CreateReviewResource to CreateReviewCommand.
 */
public class CreateReviewCommandFromResourceAssembler {
    
    public static CreateReviewCommand toCommand(CreateReviewResource resource) {
        return new CreateReviewCommand(
            resource.serviceBookingId(),
            resource.rating(),
            resource.comment()
        );
    }
}

