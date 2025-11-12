package com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.matching.domain.model.entities.Offer;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus;

/**
 * JPA repository for Offer entity.
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    /**
     * Find all offers for a service request.
     */
    @Query("SELECT o FROM Offer o WHERE o.serviceRequestId = :serviceRequestId")
    List<Offer> findByServiceRequestId(@Param("serviceRequestId") Long serviceRequestId);
    
    /**
     * Find offers by workshop.
     */
    @Query("SELECT o FROM Offer o WHERE o.workshopId.id = :workshopId")
    List<Offer> findByWorkshopId(@Param("workshopId") Long workshopId);
    
    /**
     * Find offers by workshop and status.
     */
    @Query("SELECT o FROM Offer o WHERE o.workshopId.id = :workshopId AND o.status = :status")
    List<Offer> findByWorkshopIdAndStatus(@Param("workshopId") Long workshopId, @Param("status") OfferStatus status);
    
    /**
     * Find offers for user's service requests.
     */
    @Query("SELECT o FROM Offer o JOIN ServiceRequest sr ON o.serviceRequestId = sr.id " +
           "WHERE sr.userId.id = :userId")
    List<Offer> findByUserServiceRequests(@Param("userId") Long userId);
    
    /**
     * Find offers for user's service requests filtered by status.
     */
    @Query("SELECT o FROM Offer o JOIN ServiceRequest sr ON o.serviceRequestId = sr.id " +
           "WHERE sr.userId.id = :userId AND o.status = :status")
    List<Offer> findByUserServiceRequestsAndStatus(@Param("userId") Long userId, @Param("status") OfferStatus status);
    
    /**
     * Find expired offers that are still pending.
     */
    @Query("SELECT o FROM Offer o WHERE o.status = :status AND o.expiresAt < :now")
    List<Offer> findByStatusAndExpiresAtBefore(
        @Param("status") OfferStatus status,
        @Param("now") LocalDateTime now
    );
}

