package com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for Invitation aggregate
 * <p>
 * Following DDD principles, this repository only provides basic CRUD operations.
 * Complex queries and filtering are handled in the application layer.
 * </p>
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    // Only basic CRUD operations from JpaRepository
    // Filtering is done in the application/domain layer
}

