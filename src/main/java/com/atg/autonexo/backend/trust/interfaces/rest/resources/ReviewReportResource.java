package com.atg.autonexo.backend.trust.interfaces.rest.resources;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportReason;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportStatus;

/**
 * Resource representing a review report.
 */
public record ReviewReportResource(
    Long id,
    Long reviewId,
    Long reporterId,
    ReportReason reason,
    String description,
    ReportStatus status,
    LocalDateTime reportedAt
) {}

