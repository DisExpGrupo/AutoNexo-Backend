package com.atg.autonexo.backend.trust.application.internal.commandservices;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.atg.autonexo.backend.matching.interfaces.acl.ServiceBookingFacade;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.trust.domain.exceptions.InvalidServiceBookingStatusException;
import com.atg.autonexo.backend.trust.domain.exceptions.ReviewAlreadyExistsException;
import com.atg.autonexo.backend.trust.domain.exceptions.ReviewNotFoundException;
import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.domain.model.commands.CreateReviewCommand;
import com.atg.autonexo.backend.trust.domain.model.commands.ReportReviewCommand;
import com.atg.autonexo.backend.trust.domain.model.entities.ReviewReport;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.Rating;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewType;
import com.atg.autonexo.backend.trust.domain.services.ReviewCommandService;
import com.atg.autonexo.backend.trust.domain.services.TrustScoreService;
import com.atg.autonexo.backend.trust.infrastructure.persistence.jpa.ReviewRepository;
import com.atg.autonexo.backend.workshop.application.acl.WorkshopContextFacadeImpl;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of ReviewCommandService.
 */
@Service
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {
    
    private final ReviewRepository reviewRepository;
    private final ServiceBookingFacade serviceBookingFacade;
    private final TrustScoreService trustScoreService;
    private final WorkshopContextFacadeImpl workshopFacade;
    
    @Override
    @Transactional
    public Review handle(CreateReviewCommand command) {
        // Get current user ID
        Long currentUserId = getCurrentUserId();
        
        // Check if review already exists
        boolean reviewExists = reviewRepository.existsByServiceBookingIdAndReviewerId(
            command.serviceBookingId(),
            currentUserId
        );
        
        if (reviewExists) {
            throw new ReviewAlreadyExistsException(command.serviceBookingId(), currentUserId);
        }
        
        // Get service booking info
        var bookingInfo = serviceBookingFacade.getServiceBookingInfo(command.serviceBookingId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Service booking not found: " + command.serviceBookingId()));
        
        // Validate service booking status
        if (!bookingInfo.isFinished()) {
            throw new InvalidServiceBookingStatusException(bookingInfo.status().toString());
        }
        
        // Determine review type and reviewee
        ReviewType reviewType;
        UserId revieweeUserId = null;
        WorkshopId revieweeWorkshopId = null;
        Long workshopId = getCurrentWorkshopId();
        
        if (workshopId != null) {
            // Workshop is reviewing the user
            reviewType = ReviewType.WORKSHOP_TO_USER;
            revieweeUserId = new UserId(bookingInfo.userId());
            
            // Validate workshop can review
            if (!serviceBookingFacade.validateWorkshopCanReview(command.serviceBookingId(), workshopId)) {
                throw new IllegalStateException("Workshop is not authorized to review this service");
            }
        } else {
            // User is reviewing the workshop
            reviewType = ReviewType.USER_TO_WORKSHOP;
            revieweeWorkshopId = new WorkshopId(bookingInfo.workshopId());
            
            // Validate user can review
            if (!serviceBookingFacade.validateUserCanReview(command.serviceBookingId(), currentUserId)) {
                throw new IllegalStateException("User is not authorized to review this service");
            }
        }
        
        // Get completion date
        LocalDateTime completionDate = bookingInfo.getCompletionDate();
        if (completionDate == null) {
            completionDate = LocalDateTime.now();
        }
        
        // Create review
        Review review = new Review(
            command.serviceBookingId(),
            new UserId(currentUserId),
            revieweeUserId,
            revieweeWorkshopId,
            reviewType,
            completionDate
        );
        
        // Submit the review with rating and comment
        review.submit(Rating.of(command.rating()), command.comment());
        
        // Save review
        review = reviewRepository.save(review);
        
        // Update trust score for reviewee
        if (reviewType == ReviewType.USER_TO_WORKSHOP) {
            trustScoreService.calculateAndUpdateWorkshopTrustScore(bookingInfo.workshopId());
        } else {
            trustScoreService.calculateAndUpdateUserTrustScore(bookingInfo.userId());
        }
        
        return review;
    }
    
    @Override
    @Transactional
    public ReviewReport handle(ReportReviewCommand command) {
        // Get current user ID
        Long currentUserId = getCurrentUserId();
        
        // Find review
        Review review = reviewRepository.findById(command.reviewId())
            .orElseThrow(() -> new ReviewNotFoundException(command.reviewId()));
        
        // Add report to review
        ReviewReport report = review.addReport(
            new UserId(currentUserId),
            command.reason(),
            command.description()
        );
        
        // Save review (cascade will save report)
        reviewRepository.save(review);
        
        return report;
    }
    
    @Override
    @Transactional
    public int expireAvailableReviews() {
        List<Review> expiredReviews = reviewRepository.findExpiredAvailableReviews(LocalDateTime.now());
        
        expiredReviews.forEach(Review::expire);
        
        reviewRepository.saveAll(expiredReviews);
        
        return expiredReviews.size();
    }
    
    /**
     * Get current user ID from security context.
     */
    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new SecurityException("User is not authenticated");
        }
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        throw new SecurityException("Unable to extract user ID from authentication");
    }
    
    /**
     * Get current workshop ID from workshop context (null if user is CAR_OWNER).
     */
    private Long getCurrentWorkshopId() {
        var workshopId = WorkshopContext.getCurrentWorkshopId();
        return workshopId != null ? workshopId.id() : null;
    }
}

