package com.atg.autonexo.backend.payment.infrastructure.persistence.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentStatus;

/**
 * JPA Repository for Payment aggregate.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find all payments for a workshop.
     */
    @Query("SELECT p FROM Payment p WHERE p.workshopId.id = :workshopId ORDER BY p.createdAt DESC")
    Page<Payment> findByWorkshopId(@Param("workshopId") Long workshopId, Pageable pageable);
    
    /**
     * Find payments by status.
     */
    @Query("SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByStatus(@Param("status") PaymentStatus status);
    
    /**
     * Find upcoming renewals within a date range.
     */
    @Query("SELECT p FROM Payment p WHERE p.nextBillingDate BETWEEN :from AND :to " +
           "AND p.status = 'COMPLETED' ORDER BY p.nextBillingDate ASC")
    List<Payment> findUpcomingRenewals(@Param("from") LocalDate from, @Param("to") LocalDate to);
    
    /**
     * Find payments by workshop ID and status.
     */
    @Query("SELECT p FROM Payment p WHERE p.workshopId.id = :workshopId AND p.status = :status " +
           "ORDER BY p.createdAt DESC")
    Page<Payment> findByWorkshopIdAndStatus(
        @Param("workshopId") Long workshopId,
        @Param("status") PaymentStatus status,
        Pageable pageable
    );
    
    /**
     * Find the latest completed payment for a workshop.
     */
    @Query("SELECT p FROM Payment p WHERE p.workshopId.id = :workshopId AND p.status = 'COMPLETED' " +
           "ORDER BY p.paymentDate DESC LIMIT 1")
    Payment findLatestCompletedPaymentByWorkshopId(@Param("workshopId") Long workshopId);
}

