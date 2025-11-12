package com.atg.autonexo.backend.trust.interfaces.rest.resources;

import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportReason;

/**
 * Resource for reporting a review.
 */
public record ReportReviewResource(
    ReportReason reason,
    String description
) {}

