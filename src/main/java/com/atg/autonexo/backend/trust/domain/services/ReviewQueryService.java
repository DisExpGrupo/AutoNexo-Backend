package com.atg.autonexo.backend.trust.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.domain.model.entities.ReviewReport;
import com.atg.autonexo.backend.trust.domain.model.queries.GetReviewByServiceBookingAndReviewerQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetReviewReportsQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetReviewWindowStatusQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetServiceBookingReviewsQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetUserReviewsQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetWorkshopReviewsQuery;

/**
 * Service interface for Review query operations.
 */
public interface ReviewQueryService {
    
    /**
     * Handle get review by service booking and reviewer query.
     * 
     * @param query the query
     * @return the review if found
     */
    Optional<Review> handle(GetReviewByServiceBookingAndReviewerQuery query);
    
    /**
     * Handle get workshop reviews query.
     * 
     * @param query the query
     * @return page of reviews for the workshop
     */
    Page<Review> handle(GetWorkshopReviewsQuery query);
    
    /**
     * Handle get user reviews query.
     * 
     * @param query the query
     * @return page of reviews for the user
     */
    Page<Review> handle(GetUserReviewsQuery query);
    
    /**
     * Handle get service booking reviews query.
     * Returns both reviews (user->workshop and workshop->user) if they exist.
     * 
     * @param query the query
     * @return list of reviews for the service booking (0-2 reviews)
     */
    List<Review> handle(GetServiceBookingReviewsQuery query);
    
    /**
     * Handle get review window status query.
     * Checks if a user can create a review for a service booking.
     * 
     * @param query the query
     * @return window status information
     */
    ReviewWindowStatus handle(GetReviewWindowStatusQuery query);
    
    /**
     * Handle get review reports query.
     * 
     * @param query the query
     * @return list of reports for the review
     */
    List<ReviewReport> handle(GetReviewReportsQuery query);
    
    /**
     * Find review by ID.
     * 
     * @param reviewId the review ID
     * @return the review if found
     */
    Optional<Review> findById(Long reviewId);
    
    /**
     * Value object for review window status.
     */
    record ReviewWindowStatus(
        boolean canReview,
        String reason,
        boolean reviewExists,
        boolean windowExpired,
        boolean serviceNotCompleted
    ) {}
}

