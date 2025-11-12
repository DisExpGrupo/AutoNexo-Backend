package com.atg.autonexo.backend.trust.domain.model.commands;

import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportReason;

/**
 * Command to report a review.
 */
public record ReportReviewCommand(
    Long reviewId,
    ReportReason reason,
    String description
) {
    public ReportReviewCommand {
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("Review ID must be valid");
        }
        if (reason == null) {
            throw new IllegalArgumentException("Report reason cannot be null");
        }
    }
}

