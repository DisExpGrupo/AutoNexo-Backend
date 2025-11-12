package com.atg.autonexo.backend.trust.infrastructure.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.trust.domain.model.aggregates.Review;
import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewStatus;

/**
 * JPA Repository for Review aggregate.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find review by service booking and reviewer.
     */
    @Query("SELECT r FROM Review r WHERE r.serviceBookingId = :serviceBookingId " +
           "AND r.reviewerId.id = :reviewerId")
    Optional<Review> findByServiceBookingIdAndReviewerId(
        @Param("serviceBookingId") Long serviceBookingId,
        @Param("reviewerId") Long reviewerId
    );
    
    /**
     * Check if review exists for service booking and reviewer.
     */
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.serviceBookingId = :serviceBookingId " +
           "AND r.reviewerId.id = :reviewerId")
    boolean existsByServiceBookingIdAndReviewerId(
        @Param("serviceBookingId") Long serviceBookingId,
        @Param("reviewerId") Long reviewerId
    );
    
    /**
     * Find all reviews for a workshop.
     */
    @Query("SELECT r FROM Review r WHERE r.revieweeWorkshopId.id = :workshopId " +
           "AND (:status IS NULL OR r.reviewStatus = :status) " +
           "ORDER BY r.submittedAt DESC NULLS LAST, r.createdAt DESC")
    Page<Review> findByRevieweeWorkshopId(
        @Param("workshopId") Long workshopId,
        @Param("status") ReviewStatus status,
        Pageable pageable
    );
    
    /**
     * Find all reviews for a user (reviewee).
     */
    @Query("SELECT r FROM Review r WHERE r.revieweeUserId.id = :userId " +
           "AND (:status IS NULL OR r.reviewStatus = :status) " +
           "ORDER BY r.submittedAt DESC NULLS LAST, r.createdAt DESC")
    Page<Review> findByRevieweeUserId(
        @Param("userId") Long userId,
        @Param("status") ReviewStatus status,
        Pageable pageable
    );
    
    /**
     * Find all reviews by a reviewer (user who wrote the review).
     */
    @Query("SELECT r FROM Review r WHERE r.reviewerId.id = :reviewerId " +
           "ORDER BY r.submittedAt DESC NULLS LAST, r.createdAt DESC")
    Page<Review> findByReviewerId(
        @Param("reviewerId") Long reviewerId,
        Pageable pageable
    );
    
    /**
     * Find all reviews for a service booking (both directions).
     */
    @Query("SELECT r FROM Review r WHERE r.serviceBookingId = :serviceBookingId " +
           "ORDER BY r.createdAt ASC")
    List<Review> findByServiceBookingId(@Param("serviceBookingId") Long serviceBookingId);
    
    /**
     * Count total reviews for a workshop.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.revieweeWorkshopId.id = :workshopId " +
           "AND r.reviewStatus = 'SUBMITTED'")
    long countByRevieweeWorkshopId(@Param("workshopId") Long workshopId);
    
    /**
     * Count total reviews for a user.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.revieweeUserId.id = :userId " +
           "AND r.reviewStatus = 'SUBMITTED'")
    long countByRevieweeUserId(@Param("userId") Long userId);
    
    /**
     * Find submitted reviews for a workshop within a time range.
     */
    @Query("SELECT r FROM Review r WHERE r.revieweeWorkshopId.id = :workshopId " +
           "AND r.reviewStatus = 'SUBMITTED' " +
           "AND r.submittedAt >= :since " +
           "ORDER BY r.submittedAt DESC")
    List<Review> findRecentByRevieweeWorkshopId(
        @Param("workshopId") Long workshopId,
        @Param("since") LocalDateTime since
    );
    
    /**
     * Find submitted reviews for a user within a time range.
     */
    @Query("SELECT r FROM Review r WHERE r.revieweeUserId.id = :userId " +
           "AND r.reviewStatus = 'SUBMITTED' " +
           "AND r.submittedAt >= :since " +
           "ORDER BY r.submittedAt DESC")
    List<Review> findRecentByRevieweeUserId(
        @Param("userId") Long userId,
        @Param("since") LocalDateTime since
    );
    
    /**
     * Find all available reviews that have expired.
     */
    @Query("SELECT r FROM Review r WHERE r.reviewStatus = 'AVAILABLE' " +
           "AND r.windowExpiresAt < :now")
    List<Review> findExpiredAvailableReviews(@Param("now") LocalDateTime now);
    
    /**
     * Get all unique workshop IDs that have submitted reviews.
     */
    @Query("SELECT DISTINCT r.revieweeWorkshopId.id FROM Review r " +
           "WHERE r.revieweeWorkshopId.id IS NOT NULL " +
           "AND r.reviewStatus = 'SUBMITTED'")
    List<Long> findAllWorkshopIdsWithReviews();
    
    /**
     * Get all unique user IDs that have submitted reviews.
     */
    @Query("SELECT DISTINCT r.revieweeUserId.id FROM Review r " +
           "WHERE r.revieweeUserId.id IS NOT NULL " +
           "AND r.reviewStatus = 'SUBMITTED'")
    List<Long> findAllUserIdsWithReviews();
}

