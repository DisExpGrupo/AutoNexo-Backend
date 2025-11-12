package com.atg.autonexo.backend.trust.interfaces.rest.transform;

import com.atg.autonexo.backend.trust.domain.services.TrustScoreService;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.TrustScoreResource;

/**
 * Assembler to convert TrustScoreResult to TrustScoreResource.
 */
public class TrustScoreResourceFromResultAssembler {
    
    public static TrustScoreResource toResource(TrustScoreService.TrustScoreResult result) {
        return new TrustScoreResource(
            result.trustScore(),
            result.totalReviews(),
            result.recentReviews(),
            result.averageRating(),
            result.hasMinimumReviews()
        );
    }
}

