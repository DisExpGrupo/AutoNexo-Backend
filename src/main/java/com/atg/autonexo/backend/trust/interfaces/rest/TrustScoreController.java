package com.atg.autonexo.backend.trust.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;
import com.atg.autonexo.backend.trust.domain.model.queries.GetUserTrustScoreQuery;
import com.atg.autonexo.backend.trust.domain.model.queries.GetWorkshopTrustScoreQuery;
import com.atg.autonexo.backend.trust.domain.services.TrustScoreService;
import com.atg.autonexo.backend.trust.interfaces.rest.resources.TrustScoreResource;
import com.atg.autonexo.backend.trust.interfaces.rest.transform.TrustScoreResourceFromResultAssembler;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for Trust Score operations.
 */
@RestController
@RequestMapping("/api/trust-score")
@RequiredArgsConstructor
@Tag(name = "Trust Score", description = "Trust score and reputation endpoints")
public class TrustScoreController {
    
    private final TrustScoreService trustScoreService;
    
    /**
     * Get trust score for a workshop.
     */
    @GetMapping("/workshops/{workshopId}")
    public ResponseEntity<TrustScoreResource> getWorkshopTrustScore(@PathVariable Long workshopId) {
        var query = new GetWorkshopTrustScoreQuery(workshopId);
        var result = trustScoreService.handle(query);
        var resource = TrustScoreResourceFromResultAssembler.toResource(result);
        return ResponseEntity.ok(resource);
    }
    
    /**
     * Get trust score for a user.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<TrustScoreResource> getUserTrustScore(@PathVariable Long userId) {
        var query = new GetUserTrustScoreQuery(userId);
        var result = trustScoreService.handle(query);
        var resource = TrustScoreResourceFromResultAssembler.toResource(result);
        return ResponseEntity.ok(resource);
    }
    
    /**
     * Get my trust score (current user).
     */
    @GetMapping("/my-score")
    @PreAuthorize("hasAnyRole('CAR_OWNER', 'WORKSHOP_MANAGER', 'WORKSHOP_STAFF')")
    public ResponseEntity<TrustScoreResource> getMyTrustScore() {
        Long currentUserId = getCurrentUserId();
        Long workshopId = getCurrentWorkshopId();
        
        TrustScoreService.TrustScoreResult result;
        
        if (workshopId != null) {
            // Workshop user - return workshop trust score
            result = trustScoreService.handle(new GetWorkshopTrustScoreQuery(workshopId));
        } else {
            // Regular user - return user trust score
            result = trustScoreService.handle(new GetUserTrustScoreQuery(currentUserId));
        }
        
        var resource = TrustScoreResourceFromResultAssembler.toResource(result);
        return ResponseEntity.ok(resource);
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

