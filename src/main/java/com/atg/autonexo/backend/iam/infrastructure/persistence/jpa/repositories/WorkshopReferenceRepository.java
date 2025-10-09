package com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories;

import com.atg.autonexo.backend.iam.domain.model.entities.WorkshopReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Workshop Reference Repository
 * <p>
 * This repository is responsible for managing WorkshopReference entities in the database.
 * Following DDD principles, complex queries are avoided and logic is handled in domain/application layers.
 * </p>
 */
@Repository
public interface WorkshopReferenceRepository extends JpaRepository<WorkshopReference, Long> {
    // Only basic CRUD operations from JpaRepository
}

