package com.atg.autonexo.backend.payment.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.domain.model.queries.GetPaymentByIdQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetPendingPaymentsQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetUpcomingRenewalsQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetWorkshopPaymentsQuery;
import com.atg.autonexo.backend.payment.domain.model.valueobjects.PaymentStatus;
import com.atg.autonexo.backend.payment.domain.services.PaymentQueryService;
import com.atg.autonexo.backend.payment.infrastructure.persistence.jpa.PaymentRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of PaymentQueryService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryServiceImpl implements PaymentQueryService {
    
    private final PaymentRepository paymentRepository;
    
    @Override
    public Optional<Payment> handle(GetPaymentByIdQuery query) {
        return paymentRepository.findById(query.paymentId());
    }
    
    @Override
    public Page<Payment> handle(GetWorkshopPaymentsQuery query) {
        Pageable pageable = PageRequest.of(
            query.page() != null ? query.page() : 0,
            query.size() != null ? query.size() : 20
        );
        
        return paymentRepository.findByWorkshopId(query.workshopId(), pageable);
    }
    
    @Override
    public List<Payment> handle(GetPendingPaymentsQuery query) {
        return paymentRepository.findByStatus(PaymentStatus.PENDING);
    }
    
    @Override
    public List<Payment> handle(GetUpcomingRenewalsQuery query) {
        return paymentRepository.findUpcomingRenewals(query.from(), query.to());
    }
}

