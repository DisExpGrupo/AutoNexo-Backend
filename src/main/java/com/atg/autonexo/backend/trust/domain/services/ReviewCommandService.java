package com.atg.autonexo.backend.trust.domain.services;

import java.util.Optional;

import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.domain.model.commands.CreateReviewCommand;
import com.atg.autonexo.backend.trust.domain.model.commands.ReportReviewCommand;
import com.atg.autonexo.backend.trust.domain.model.entities.ReviewReport;

/**
 * Service interface for Review command operations.
 */
public interface ReviewCommandService {
    
    /**
     * Handle create review command.
     * Creates and submits a review for a service booking.
     * 
     * @param command the create review command
     * @return the created review
     */
    Review handle(CreateReviewCommand command);
    
    /**
     * Handle report review command.
     * Creates a report for an existing review.
     * 
     * @param command the report review command
     * @return the created review report
     */
    ReviewReport handle(ReportReviewCommand command);
    
    /**
     * Expire reviews that have passed their window.
     * Called by scheduled task.
     * 
     * @return number of reviews expired
     */
    int expireAvailableReviews();
}

