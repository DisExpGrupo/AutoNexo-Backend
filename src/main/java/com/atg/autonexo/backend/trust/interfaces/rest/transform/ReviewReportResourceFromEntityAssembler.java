package com.atg.autonexo.backend.trust.interfaces.rest.transform;

import com.atg.autonexo.backend.trust.domain.model.entities.ReviewReport;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.ReviewReportResource;

/**
 * Assembler to convert ReviewReport entity to ReviewReportResource.
 */
public class ReviewReportResourceFromEntityAssembler {
    
    public static ReviewReportResource toResource(ReviewReport report) {
        return new ReviewReportResource(
            report.getId(),
            report.getReviewId(),
            report.getReporterId() != null ? report.getReporterId().id() : null,
            report.getReason(),
            report.getDescription(),
            report.getStatus(),
            report.getReportedAt()
        );
    }
}

