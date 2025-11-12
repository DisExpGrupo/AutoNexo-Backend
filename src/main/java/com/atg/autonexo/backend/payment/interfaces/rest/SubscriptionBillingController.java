package com.atg.autonexo.backend.payment.interfaces.rest;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.payment.domain.model.queries.GetUpcomingRenewalsQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetWorkshopPaymentsQuery;
import com.atg.autonexo.backend.payment.domain.services.PaymentQueryService;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.PaymentSummaryResource;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.UpcomingRenewalResource;
import com.atg.autonexo.backend.payment.interfaces.rest.transform.PaymentSummaryResourceFromEntityAssembler;
import com.atg.autonexo.backend.payment.interfaces.rest.transform.UpcomingRenewalResourceFromEntityAssembler;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for subscription billing information.
 */
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Subscription billing endpoints")
public class SubscriptionBillingController {
    
    private final PaymentQueryService paymentQueryService;
    
    /**
     * Get upcoming renewal information.
     */
    @GetMapping("/upcoming-renewals")
    @PreAuthorize("hasAnyRole('WORKSHOP_MANAGER', 'ADMIN')")
    @Operation(summary = "Get upcoming renewals", description = "Get information about upcoming subscription renewals")
    public ResponseEntity<List<UpcomingRenewalResource>> getUpcomingRenewals(
            @RequestParam(defaultValue = "7") int daysAhead) {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(daysAhead);
        
        var query = new GetUpcomingRenewalsQuery(from, to);
        var payments = paymentQueryService.handle(query);
        var renewals = payments.stream()
            .map(UpcomingRenewalResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
        
        return ResponseEntity.ok(renewals);
    }
    
    /**
     * Get billing history for current workshop.
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('WORKSHOP_MANAGER')")
    @Operation(summary = "Get billing history", description = "Get payment history for the current workshop")
    public ResponseEntity<Page<PaymentSummaryResource>> getBillingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long workshopId = getCurrentWorkshopId();
        var query = new GetWorkshopPaymentsQuery(workshopId, page, size);
        var payments = paymentQueryService.handle(query);
        var summaries = payments.map(PaymentSummaryResourceFromEntityAssembler::toResourceFromEntity);
        return ResponseEntity.ok(summaries);
    }
    
    /**
     * Get current workshop ID from context.
     */
    private Long getCurrentWorkshopId() {
        return WorkshopContext.getCurrentWorkshopIdAsLong();
    }
}

