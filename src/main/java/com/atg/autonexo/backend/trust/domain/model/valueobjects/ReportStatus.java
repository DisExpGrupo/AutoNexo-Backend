package com.atg.autonexo.backend.trust.domain.model.valueobjects;

/**
 * Enum representing the status of a review report.
 */
public enum ReportStatus {
    /**
     * Report is pending review by moderators
     */
    PENDING,
    
    /**
     * Report has been reviewed and action taken
     */
    REVIEWED,
    
    /**
     * Report was reviewed and dismissed as invalid
     */
    DISMISSED
}

