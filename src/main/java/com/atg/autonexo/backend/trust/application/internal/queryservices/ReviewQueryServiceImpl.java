package com.atg.autonexo.backend.trust.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.interfaces.acl.ServiceBookingFacade;
import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.domain.model.entities.ReviewReport;
import com.atg.autonexo.backend.trust.domain.model.queries.GetReviewByServiceBookingAndReviewerQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetReviewReportsQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetReviewWindowStatusQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetServiceBookingReviewsQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetUserReviewsQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetWorkshopReviewsQuery;
import com.atg.autonexo.backend.trust.domain.services.ReviewQueryService;
import com.atg.autonexo.backend.trust.infrastructure.persistence.jpa.ReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of ReviewQueryService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl implements ReviewQueryService {
    
    private final ReviewRepository reviewRepository;
    private final ServiceBookingFacade serviceBookingFacade;
    
    @Override
    public Optional<Review> handle(GetReviewByServiceBookingAndReviewerQuery query) {
        return reviewRepository.findByServiceBookingIdAndReviewerId(
            query.serviceBookingId(),
            query.reviewerId()
        );
    }
    
    @Override
    public Page<Review> handle(GetWorkshopReviewsQuery query) {
        Pageable pageable = PageRequest.of(
            query.page() != null ? query.page() : 0,
            query.size() != null ? query.size() : 20
        );
        
        return reviewRepository.findByRevieweeWorkshopId(
            query.workshopId(),
            query.status(),
            pageable
        );
    }
    
    @Override
    public Page<Review> handle(GetUserReviewsQuery query) {
        Pageable pageable = PageRequest.of(
            query.page() != null ? query.page() : 0,
            query.size() != null ? query.size() : 20
        );
        
        return reviewRepository.findByRevieweeUserId(
            query.userId(),
            query.status(),
            pageable
        );
    }
    
    @Override
    public List<Review> handle(GetServiceBookingReviewsQuery query) {
        return reviewRepository.findByServiceBookingId(query.serviceBookingId());
    }
    
    @Override
    public ReviewWindowStatus handle(GetReviewWindowStatusQuery query) {
        // Check if review already exists
        boolean reviewExists = reviewRepository.existsByServiceBookingIdAndReviewerId(
            query.serviceBookingId(),
            query.userId()
        );
        
        if (reviewExists) {
            return new ReviewWindowStatus(
                false,
                "Review already submitted for this service",
                true,
                false,
                false
            );
        }
        
        // Get service booking info
        var bookingInfo = serviceBookingFacade.getServiceBookingInfo(query.serviceBookingId());
        
        if (bookingInfo.isEmpty()) {
            return new ReviewWindowStatus(
                false,
                "Service booking not found",
                false,
                false,
                true
            );
        }
        
        var booking = bookingInfo.get();
        
        // Check if service is completed
        if (!booking.isFinished()) {
            return new ReviewWindowStatus(
                false,
                "Service is not yet completed or cancelled",
                false,
                false,
                true
            );
        }
        
        // Check if window expired (14 days)
        var completionDate = booking.getCompletionDate();
        if (completionDate != null && completionDate.plusDays(14).isBefore(java.time.LocalDateTime.now())) {
            return new ReviewWindowStatus(
                false,
                "Review window has expired (14 days)",
                false,
                true,
                false
            );
        }
        
        // User can review
        return new ReviewWindowStatus(
            true,
            "Can create review",
            false,
            false,
            false
        );
    }
    
    @Override
    public List<ReviewReport> handle(GetReviewReportsQuery query) {
        return reviewRepository.findById(query.reviewId())
            .map(Review::getReports)
            .orElse(List.of());
    }
    
    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }
}

