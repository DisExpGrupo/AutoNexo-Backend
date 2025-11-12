package com.atg.autonexo.backend.trust.interfaces.rest.transform;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.ReviewResource;

/**
 * Assembler to convert Review entity to ReviewResource.
 */
public class ReviewResourceFromEntityAssembler {
    
    public static ReviewResource toResource(Review review) {
        return new ReviewResource(
            review.getId(),
            review.getServiceBookingId(),
            review.getReviewerId() != null ? review.getReviewerId().id() : null,
            review.getRevieweeUserId() != null ? review.getRevieweeUserId().id() : null,
            review.getRevieweeWorkshopId() != null ? review.getRevieweeWorkshopId().id() : null,
            review.getReviewType(),
            review.getRating() != null ? review.getRating().value() : null,
            review.getComment(),
            review.getReviewStatus(),
            review.getSubmittedAt(),
            review.getWindowExpiresAt(),
            (long) review.getReports().size(),
            review.getCreatedAt() != null ? 
                LocalDateTime.ofInstant(review.getCreatedAt().toInstant(), ZoneId.systemDefault()) : null
        );
    }
}

