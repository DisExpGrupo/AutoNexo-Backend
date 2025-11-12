package com.atg.autonexo.backend.trust.domain.model.entities;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportReason;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a report filed against a review.
 * Part of the Review aggregate.
 */
@Entity
@Getter
@Setter
public class ReviewReport extends AuditableModel {
    
    @Column(name = "review_id", nullable = false, insertable = false, updatable = false)
    private Long reviewId;
    
    @Embedded
    private UserId reporterId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportReason reason;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;
    
    @Column(nullable = false)
    private LocalDateTime reportedAt;
    
    protected ReviewReport() {}
    
    public ReviewReport(Long reviewId, UserId reporterId, ReportReason reason, String description) {
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("Review ID must be valid.");
        }
        if (reporterId == null) {
            throw new IllegalArgumentException("Reporter ID cannot be null.");
        }
        if (reason == null) {
            throw new IllegalArgumentException("Report reason cannot be null.");
        }
        
        this.reviewId = reviewId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.description = description;
        this.status = ReportStatus.PENDING;
        this.reportedAt = LocalDateTime.now();
    }
    
    public void markAsReviewed() {
        this.status = ReportStatus.REVIEWED;
    }
    
    public void dismiss() {
        this.status = ReportStatus.DISMISSED;
    }
}

