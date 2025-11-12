package com.atg.autonexo.backend.trust.application.internal.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.trust.domain.services.ReviewCommandService;

import lombok.RequiredArgsConstructor;

/**
 * Scheduled service to expire review windows that have passed their 14-day deadline.
 * Runs daily at 3 AM.
 */
@Service
@RequiredArgsConstructor
public class ReviewWindowExpirationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewWindowExpirationService.class);
    
    private final ReviewCommandService reviewCommandService;
    
    /**
     * Expire reviews that have passed their window.
     * Runs daily at 3:00 AM.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void expireReviewWindows() {
        LOGGER.info("Starting review window expiration task");
        
        try {
            int expiredCount = reviewCommandService.expireAvailableReviews();
            LOGGER.info("Review window expiration task completed. Expired {} reviews", expiredCount);
        } catch (Exception e) {
            LOGGER.error("Error during review window expiration task: {}", e.getMessage(), e);
        }
    }
}

