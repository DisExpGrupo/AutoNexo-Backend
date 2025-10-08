package com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Invitation aggregate
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    /**
     * Finds an invitation by code
     */
    @Query("SELECT i FROM Invitation i WHERE i.invitationCode.value = :code")
    Optional<Invitation> findByCode(@Param("code") String code);
    
    /**
     * Finds all invitations for a workshop
     */
    @Query("SELECT i FROM Invitation i WHERE i.workshopId.id = :workshopId ORDER BY i.createdAt DESC")
    List<Invitation> findByWorkshopId(@Param("workshopId") Long workshopId);
    
    /**
     * Finds pending (unused and not expired) invitations for a workshop
     */
    @Query("SELECT i FROM Invitation i WHERE i.workshopId.id = :workshopId AND i.used = false AND i.expiresAt > CURRENT_TIMESTAMP")
    List<Invitation> findPendingByWorkshopId(@Param("workshopId") Long workshopId);
    
    /**
     * Checks if an invitation code exists
     */
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.invitationCode.value = :code")
    boolean existsByCode(@Param("code") String code);
}

