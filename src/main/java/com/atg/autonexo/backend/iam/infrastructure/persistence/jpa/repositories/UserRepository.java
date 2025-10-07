package com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
/**
 * User Repository
 * <p>
 * This repository is responsible for managing User entities in the database.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by email
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists by email
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by workshop ID
     * @param workshopId the workshop ID
     * @return List of users for the workshop
     */
    List<User> findAllByWorkshopId(Long workshopId);
    
} 