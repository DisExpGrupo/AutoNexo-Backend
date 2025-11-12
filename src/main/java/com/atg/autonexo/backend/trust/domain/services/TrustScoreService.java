package com.atg.autonexo.backend.trust.domain.services;

import com.atg.autonexo.backend.trust.domain.model.queries.GetUserTrustScoreQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetWorkshopTrustScoreQuery;

/**
 * Service interface for trust score calculation and management.
 */
public interface TrustScoreService {
    
    /**
     * Calculate and update workshop trust score.
     * Uses weighted algorithm based on review recency.
     * 
     * @param workshopId the workshop ID
     * @return the calculated trust score
     */
    TrustScoreResult calculateAndUpdateWorkshopTrustScore(Long workshopId);
    
    /**
     * Calculate and update user trust score.
     * Uses weighted algorithm based on review recency.
     * 
     * @param userId the user ID
     * @return the calculated trust score
     */
    TrustScoreResult calculateAndUpdateUserTrustScore(Long userId);
    
    /**
     * Handle get workshop trust score query.
     * 
     * @param query the query
     * @return trust score result with statistics
     */
    TrustScoreResult handle(GetWorkshopTrustScoreQuery query);
    
    /**
     * Handle get user trust score query.
     * 
     * @param query the query
     * @return trust score result with statistics
     */
    TrustScoreResult handle(GetUserTrustScoreQuery query);
    
    /**
     * Recalculate all workshop trust scores.
     * Called by scheduled task.
     * 
     * @return number of workshops updated
     */
    int recalculateAllWorkshopTrustScores();
    
    /**
     * Recalculate all user trust scores.
     * Called by scheduled task.
     * 
     * @return number of users updated
     */
    int recalculateAllUserTrustScores();
    
    /**
     * Result object for trust score calculations.
     */
    record TrustScoreResult(
        Float trustScore,
        Long totalReviews,
        Long recentReviews,
        Double averageRating,
        boolean hasMinimumReviews
    ) {
        public static TrustScoreResult empty() {
            return new TrustScoreResult(null, 0L, 0L, 0.0, false);
        }
    }
}

