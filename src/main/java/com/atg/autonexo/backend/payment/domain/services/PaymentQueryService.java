package com.atg.autonexo.backend.payment.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.domain.model.queries.GetPaymentByIdQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetPendingPaymentsQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetUpcomingRenewalsQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetWorkshopPaymentsQuery;

/**
 * Service interface for Payment query operations.
 */
public interface PaymentQueryService {
    
    /**
     * Handle get payment by ID query.
     */
    Optional<Payment> handle(GetPaymentByIdQuery query);
    
    /**
     * Handle get workshop payments query.
     */
    Page<Payment> handle(GetWorkshopPaymentsQuery query);
    
    /**
     * Handle get pending payments query.
     */
    List<Payment> handle(GetPendingPaymentsQuery query);
    
    /**
     * Handle get upcoming renewals query.
     */
    List<Payment> handle(GetUpcomingRenewalsQuery query);
}

