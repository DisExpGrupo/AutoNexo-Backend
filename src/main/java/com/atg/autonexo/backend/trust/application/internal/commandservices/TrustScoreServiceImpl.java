package com.atg.autonexo.backend.trust.application.internal.commandservices;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.interfaces.acl.IamFacade;
import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.domain.model.queries.GetUserTrustScoreQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetWorkshopTrustScoreQuery;
import com.atg.autonexo.backend.trust.domain.services.TrustScoreService;
import com.atg.autonexo.backend.trust.infrastructure.persistence.jpa.ReviewRepository;
import com.atg.autonexo.backend.workshop.interfaces.acl.WorkshopContextFacade;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of TrustScoreService with weighted algorithm.
 */
@Service
@RequiredArgsConstructor
public class TrustScoreServiceImpl implements TrustScoreService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TrustScoreServiceImpl.class);
    private static final int MINIMUM_REVIEWS = 3;
    
    private final ReviewRepository reviewRepository;
    private final WorkshopContextFacade workshopFacade;
    private final IamFacade iamFacade;
    
    @Override
    @Transactional
    public TrustScoreResult calculateAndUpdateWorkshopTrustScore(Long workshopId) {
        LOGGER.info("Calculating trust score for workshop: {}", workshopId);
        
        TrustScoreResult result = calculateWorkshopTrustScore(workshopId);
        
        if (result.hasMinimumReviews() && result.trustScore() != null) {
            workshopFacade.updateWorkshopTrustScore(workshopId, result.trustScore());
            LOGGER.info("Updated workshop {} trust score to {}", workshopId, result.trustScore());
        } else {
            // Not enough reviews, set to null
            workshopFacade.updateWorkshopTrustScore(workshopId, null);
            LOGGER.info("Workshop {} does not have minimum reviews. Trust score set to null", workshopId);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public TrustScoreResult calculateAndUpdateUserTrustScore(Long userId) {
        LOGGER.info("Calculating trust score for user: {}", userId);
        
        TrustScoreResult result = calculateUserTrustScore(userId);
        
        if (result.hasMinimumReviews() && result.trustScore() != null) {
            iamFacade.updateUserTrustScore(userId, result.trustScore());
            LOGGER.info("Updated user {} trust score to {}", userId, result.trustScore());
        } else {
            // Not enough reviews, set to null
            iamFacade.updateUserTrustScore(userId, null);
            LOGGER.info("User {} does not have minimum reviews. Trust score set to null", userId);
        }
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrustScoreResult handle(GetWorkshopTrustScoreQuery query) {
        return calculateWorkshopTrustScore(query.workshopId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrustScoreResult handle(GetUserTrustScoreQuery query) {
        return calculateUserTrustScore(query.userId());
    }
    
    @Override
    @Transactional
    public int recalculateAllWorkshopTrustScores() {
        LOGGER.info("Starting workshop trust score recalculation");
        
        List<Long> workshopIds = reviewRepository.findAllWorkshopIdsWithReviews();
        
        int count = 0;
        for (Long workshopId : workshopIds) {
            try {
                calculateAndUpdateWorkshopTrustScore(workshopId);
                count++;
            } catch (Exception e) {
                LOGGER.error("Error recalculating trust score for workshop {}: {}", 
                    workshopId, e.getMessage(), e);
            }
        }
        
        LOGGER.info("Completed workshop trust score recalculation. Updated {} workshops", count);
        return count;
    }
    
    @Override
    @Transactional
    public int recalculateAllUserTrustScores() {
        LOGGER.info("Starting user trust score recalculation");
        
        List<Long> userIds = reviewRepository.findAllUserIdsWithReviews();
        
        int count = 0;
        for (Long userId : userIds) {
            try {
                calculateAndUpdateUserTrustScore(userId);
                count++;
            } catch (Exception e) {
                LOGGER.error("Error recalculating trust score for user {}: {}", 
                    userId, e.getMessage(), e);
            }
        }
        
        LOGGER.info("Completed user trust score recalculation. Updated {} users", count);
        return count;
    }
    
    /**
     * Calculate workshop trust score using weighted algorithm.
     * Recent reviews (0-6 months): 100% weight
     * Medium reviews (6-12 months): 50% weight
     * Old reviews (12-24 months): 20% weight
     * Very old reviews (>24 months): 10% weight
     */
    private TrustScoreResult calculateWorkshopTrustScore(Long workshopId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get all submitted reviews
        List<Review> allReviews = reviewRepository.findRecentByRevieweeWorkshopId(
            workshopId,
            now.minusYears(3) // Get last 3 years
        );
        
        if (allReviews.size() < MINIMUM_REVIEWS) {
            return TrustScoreResult.empty();
        }
        
        // Calculate weighted score
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        long recentCount = 0;
        
        for (Review review : allReviews) {
            if (review.getRating() == null) {
                continue;
            }
            
            LocalDateTime submittedAt = review.getSubmittedAt();
            double weight = calculateWeight(submittedAt, now);
            double rating = review.getRating().value();
            
            weightedSum += rating * weight;
            totalWeight += weight;
            
            // Count recent reviews (last 6 months)
            if (submittedAt.isAfter(now.minusMonths(6))) {
                recentCount++;
            }
        }
        
        if (totalWeight == 0) {
            return TrustScoreResult.empty();
        }
        
        // Calculate average rating
        double averageRating = weightedSum / totalWeight;
        
        // Normalize to 0-5 scale (trust score)
        float trustScore = (float) averageRating;
        
        return new TrustScoreResult(
            trustScore,
            (long) allReviews.size(),
            recentCount,
            averageRating,
            true
        );
    }
    
    /**
     * Calculate user trust score using weighted algorithm (same as workshop).
     */
    private TrustScoreResult calculateUserTrustScore(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get all submitted reviews
        List<Review> allReviews = reviewRepository.findRecentByRevieweeUserId(
            userId,
            now.minusYears(3) // Get last 3 years
        );
        
        if (allReviews.size() < MINIMUM_REVIEWS) {
            return TrustScoreResult.empty();
        }
        
        // Calculate weighted score
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        long recentCount = 0;
        
        for (Review review : allReviews) {
            if (review.getRating() == null) {
                continue;
            }
            
            LocalDateTime submittedAt = review.getSubmittedAt();
            double weight = calculateWeight(submittedAt, now);
            double rating = review.getRating().value();
            
            weightedSum += rating * weight;
            totalWeight += weight;
            
            // Count recent reviews (last 6 months)
            if (submittedAt.isAfter(now.minusMonths(6))) {
                recentCount++;
            }
        }
        
        if (totalWeight == 0) {
            return TrustScoreResult.empty();
        }
        
        // Calculate average rating
        double averageRating = weightedSum / totalWeight;
        
        // Normalize to 0-5 scale (trust score)
        float trustScore = (float) averageRating;
        
        return new TrustScoreResult(
            trustScore,
            (long) allReviews.size(),
            recentCount,
            averageRating,
            true
        );
    }
    
    /**
     * Calculate weight based on review age.
     * 0-6 months: 1.0 (100%)
     * 6-12 months: 0.7 (70%)
     * 12-24 months: 0.3 (30%)
     * >24 months: 0.1 (10%)
     */
    private double calculateWeight(LocalDateTime submittedAt, LocalDateTime now) {
        long monthsAgo = java.time.temporal.ChronoUnit.MONTHS.between(submittedAt, now);
        
        if (monthsAgo <= 6) {
            return 1.0; // Full weight
        } else if (monthsAgo <= 12) {
            return 0.7; // 70% weight
        } else if (monthsAgo <= 24) {
            return 0.3; // 30% weight
        } else {
            return 0.1; // 10% weight
        }
    }
}

