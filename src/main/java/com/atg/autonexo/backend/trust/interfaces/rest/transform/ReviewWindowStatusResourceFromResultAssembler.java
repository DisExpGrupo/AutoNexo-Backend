package com.atg.autonexo.backend.trust.interfaces.rest.transform;

import com.atg.autonexo.backend.trust.domain.services.ReviewQueryService;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.ReviewWindowStatusResource;

/**
 * Assembler to convert ReviewWindowStatus to ReviewWindowStatusResource.
 */
public class ReviewWindowStatusResourceFromResultAssembler {
    
    public static ReviewWindowStatusResource toResource(ReviewQueryService.ReviewWindowStatus status) {
        return new ReviewWindowStatusResource(
            status.canReview(),
            status.reason(),
            status.reviewExists(),
            status.windowExpired(),
            status.serviceNotCompleted()
        );
    }
}

