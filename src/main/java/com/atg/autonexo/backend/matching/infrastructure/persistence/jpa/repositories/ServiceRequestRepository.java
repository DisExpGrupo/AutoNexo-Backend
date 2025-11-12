package com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceRequestStatus;

/**
 * JPA repository for ServiceRequest aggregate.
 */
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    
    /**
     * Find all service requests for a user.
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.userId.id = :userId")
    List<ServiceRequest> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find service requests for a user filtered by status.
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.userId.id = :userId AND sr.status = :status")
    List<ServiceRequest> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ServiceRequestStatus status);
    
    /**
     * Find service requests that are not rejected by a specific workshop.
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = :status " +
           "AND (:workshopId NOT MEMBER OF sr.rejectedByWorkshops)")
    List<ServiceRequest> findByStatusAndNotRejectedByWorkshop(
        @Param("status") ServiceRequestStatus status,
        @Param("workshopId") Long workshopId
    );
    
    /**
     * Find service requests that a workshop has sent offers to.
     */
    @Query("SELECT DISTINCT sr FROM ServiceRequest sr JOIN sr.offers o " +
           "WHERE o.workshopId.id = :workshopId")
    List<ServiceRequest> findByWorkshopOffers(@Param("workshopId") Long workshopId);
}

