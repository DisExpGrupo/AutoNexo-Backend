package com.atg.autonexo.backend.trust.interfaces.rest.transform;

import com.atg.autonexo.backend.trust.domain.model.commands.ReportReviewCommand;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.ReportReviewResource;

/**
 * Assembler to convert ReportReviewResource to ReportReviewCommand.
 */
public class ReportReviewCommandFromResourceAssembler {
    
    public static ReportReviewCommand toCommand(Long reviewId, ReportReviewResource resource) {
        return new ReportReviewCommand(
            reviewId,
            resource.reason(),
            resource.description()
        );
    }
}

