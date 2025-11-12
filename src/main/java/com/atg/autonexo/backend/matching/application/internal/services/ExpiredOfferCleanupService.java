package com.atg.autonexo.backend.matching.application.internal.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.OfferRepository;

/**
 * Scheduled service for cleaning up expired offers.
 * Runs daily at 2 AM to mark expired offers as EXPIRED.
 */
@Service
public class ExpiredOfferCleanupService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredOfferCleanupService.class);
    
    private final OfferRepository offerRepository;
    
    public ExpiredOfferCleanupService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }
    
    /**
     * Scheduled task that runs daily at 2 AM to mark expired offers.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    @Transactional
    public void cleanupExpiredOffers() {
        LOGGER.info("Starting expired offers cleanup");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Offer> expiredOffers = offerRepository.findByStatusAndExpiresAtBefore(
                OfferStatus.PENDING,
                now
            );
            
            int expiredCount = 0;
            for (Offer offer : expiredOffers) {
                offer.markAsExpired();
                offerRepository.save(offer);
                expiredCount++;
            }
            
            LOGGER.info("Expired offers cleanup completed. Marked {} offers as expired", expiredCount);
        } catch (Exception e) {
            LOGGER.error("Error during expired offers cleanup", e);
        }
    }
}

