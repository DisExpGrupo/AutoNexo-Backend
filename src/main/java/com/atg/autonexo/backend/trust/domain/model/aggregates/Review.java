package com.atg.autonexo.backend.trust.domain.model.aggregates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.trust.domain.exceptions.ReviewWindowExpiredException;
import com.atg.autonexo.backend.trust.domain.model.entities.ReviewReport;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.Rating;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportReason;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewStatus;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Aggregate root representing a review for a service booking.
 * Can be from user to workshop or from workshop to user.
 */
@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "reviews")
public class Review extends AuditableAbstractAggregateRoot<Review> {
    
    @Column(nullable = false)
    private Long serviceBookingId;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "reviewer_user_id", nullable = false))
    })
    private UserId reviewerId; // Who is writing the review
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "reviewee_user_id"))
    })
    private UserId revieweeUserId; // If reviewing a user (nullable)
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "reviewee_workshop_id"))
    })
    private WorkshopId revieweeWorkshopId; // If reviewing a workshop (nullable)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewType reviewType;
    
    @Embedded
    private Rating rating;
    
    @Column(length = 1000)
    private String comment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus reviewStatus;
    
    @Column
    private LocalDateTime submittedAt;
    
    @Column(nullable = false)
    private LocalDateTime windowExpiresAt;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private List<ReviewReport> reports = new ArrayList<>();
    
    protected Review() {}
    
    /**
     * Creates a new review (not yet submitted).
     */
    public Review(Long serviceBookingId, UserId reviewerId, UserId revieweeUserId, 
                  WorkshopId revieweeWorkshopId, ReviewType reviewType, LocalDateTime completionDate) {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("Service booking ID must be valid.");
        }
        if (reviewerId == null) {
            throw new IllegalArgumentException("Reviewer ID cannot be null.");
        }
        if (reviewType == null) {
            throw new IllegalArgumentException("Review type cannot be null.");
        }
        if (completionDate == null) {
            throw new IllegalArgumentException("Completion date cannot be null.");
        }
        
        // Validate reviewee based on type
        if (reviewType == ReviewType.USER_TO_WORKSHOP && revieweeWorkshopId == null) {
            throw new IllegalArgumentException("Workshop ID cannot be null for USER_TO_WORKSHOP review.");
        }
        if (reviewType == ReviewType.WORKSHOP_TO_USER && revieweeUserId == null) {
            throw new IllegalArgumentException("User ID cannot be null for WORKSHOP_TO_USER review.");
        }
        
        this.serviceBookingId = serviceBookingId;
        this.reviewerId = reviewerId;
        this.revieweeUserId = revieweeUserId;
        this.revieweeWorkshopId = revieweeWorkshopId;
        this.reviewType = reviewType;
        this.reviewStatus = ReviewStatus.AVAILABLE;
        this.windowExpiresAt = completionDate.plusDays(14); // 14 day window
    }
    
    /**
     * Submit the review with rating and optional comment.
     */
    public void submit(Rating rating, String comment) {
        if (this.reviewStatus != ReviewStatus.AVAILABLE) {
            throw new IllegalStateException("Review can only be submitted when status is AVAILABLE.");
        }
        
        if (LocalDateTime.now().isAfter(this.windowExpiresAt)) {
            throw new ReviewWindowExpiredException(this.windowExpiresAt);
        }
        
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null.");
        }
        
        this.rating = rating;
        this.comment = comment;
        this.reviewStatus = ReviewStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }
    
    /**
     * Mark review window as expired.
     */
    public void expire() {
        if (this.reviewStatus == ReviewStatus.AVAILABLE && 
            LocalDateTime.now().isAfter(this.windowExpiresAt)) {
            this.reviewStatus = ReviewStatus.EXPIRED;
        }
    }
    
    /**
     * Add a report to this review.
     */
    public ReviewReport addReport(UserId reporterId, ReportReason reason, String description) {
        if (this.reviewStatus != ReviewStatus.SUBMITTED) {
            throw new IllegalStateException("Can only report a submitted review.");
        }
        
        ReviewReport report = new ReviewReport(this.getId(), reporterId, reason, description);
        this.reports.add(report);
        return report;
    }
    
    /**
     * Check if review window is still open.
     */
    public boolean isWindowOpen() {
        return this.reviewStatus == ReviewStatus.AVAILABLE && 
               LocalDateTime.now().isBefore(this.windowExpiresAt);
    }
    
    /**
     * Check if review has been submitted.
     */
    public boolean isSubmitted() {
        return this.reviewStatus == ReviewStatus.SUBMITTED;
    }
    
    /**
     * Get the number of pending reports.
     */
    public long getPendingReportsCount() {
        return this.reports.stream()
            .filter(r -> r.getStatus() == com.atg.autonexo.backend.trust.domain.model.valueobjects.ReportStatus.PENDING)
            .count();
    }
}

