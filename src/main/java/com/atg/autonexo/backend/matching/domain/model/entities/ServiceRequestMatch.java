package com.atg.autonexo.backend.matching.domain.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

/**
 * ServiceRequestMatch entity.
 * Represents a matched workshop for a service request.
 * Part of the ServiceRequest aggregate.
 */
@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "service_request_matches")
public class ServiceRequestMatch extends AuditableModel {
    
    @Embedded
    private WorkshopId workshopId;
    
    @Column(nullable = false)
    private Double matchScore;
    
    @Column(nullable = false)
    private Double distanceKm;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @jakarta.persistence.CollectionTable(name = "match_services", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "service")
    @Enumerated(EnumType.STRING)
    private List<ServiceCatalog> matchingServices = new ArrayList<>();
    
    @Column(nullable = false)
    private boolean notificationSent = false;
    
    @Column
    private LocalDateTime notificationSentAt;
    
    protected ServiceRequestMatch() {}
    
    /**
     * Creates a new service request match.
     * 
     * @param workshopId The matched workshop ID
     * @param matchScore The match score (higher is better)
     * @param distanceKm Distance from user to workshop in kilometers
     * @param matchingServices List of services that match between request and workshop
     */
    public ServiceRequestMatch(WorkshopId workshopId, Double matchScore, Double distanceKm, 
                               List<ServiceCatalog> matchingServices) {
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        if (matchScore == null || matchScore < 0) {
            throw new IllegalArgumentException("MatchScore must be non-negative");
        }
        if (distanceKm == null || distanceKm < 0) {
            throw new IllegalArgumentException("DistanceKm must be non-negative");
        }
        if (matchingServices == null || matchingServices.isEmpty()) {
            matchingServices = new ArrayList<>();
            //throw new IllegalArgumentException("MatchingServices cannot be null or empty");
        }
        
        this.workshopId = workshopId;
        this.matchScore = matchScore;
        this.distanceKm = distanceKm;
        this.matchingServices = new ArrayList<>(matchingServices);
        this.notificationSent = false;
    }
    
    /**
     * Marks that a notification has been sent for this match.
     */
    public void markNotificationSent() {
        this.notificationSent = true;
        this.notificationSentAt = LocalDateTime.now();
    }
    
    /**
     * Checks if notification has been sent.
     */
    public boolean isNotificationSent() {
        return notificationSent;
    }
}

