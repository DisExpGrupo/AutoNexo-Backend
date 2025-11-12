package com.atg.autonexo.backend.trust.domain.model.valueobjects;

/**
 * Enum representing the reason for reporting a review.
 */
public enum ReportReason {
    /**
     * Review contains inappropriate or offensive content
     */
    INAPPROPRIATE_CONTENT,
    
    /**
     * Review is spam or irrelevant
     */
    SPAM,
    
    /**
     * Review appears to be fake or fraudulent
     */
    FAKE_REVIEW,
    
    /**
     * Review contains offensive language
     */
    OFFENSIVE_LANGUAGE,
    
    /**
     * Other reason not covered by the above
     */
    OTHER
}

