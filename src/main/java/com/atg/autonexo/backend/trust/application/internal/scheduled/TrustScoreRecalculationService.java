package com.atg.autonexo.backend.trust.application.internal.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.trust.domain.services.TrustScoreService;

import lombok.RequiredArgsConstructor;

/**
 * Scheduled service to recalculate trust scores for all workshops and users.
 * Runs weekly on Sundays at 2 AM.
 */
@Service
@RequiredArgsConstructor
public class TrustScoreRecalculationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TrustScoreRecalculationService.class);
    
    private final TrustScoreService trustScoreService;
    
    /**
     * Recalculate trust scores for all workshops and users.
     * Runs every Sunday at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void recalculateTrustScores() {
        LOGGER.info("Starting trust score recalculation task");
        
        try {
            // Recalculate workshop trust scores
            int workshopsUpdated = trustScoreService.recalculateAllWorkshopTrustScores();
            LOGGER.info("Workshop trust score recalculation completed. Updated {} workshops", workshopsUpdated);
            
            // Recalculate user trust scores
            int usersUpdated = trustScoreService.recalculateAllUserTrustScores();
            LOGGER.info("User trust score recalculation completed. Updated {} users", usersUpdated);
            
            LOGGER.info("Trust score recalculation task completed successfully");
        } catch (Exception e) {
            LOGGER.error("Error during trust score recalculation task: {}", e.getMessage(), e);
        }
    }
}

