package com.atg.autonexo.backend.payment.interfaces.rest;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.payment.domain.model.aggregates.Payment;
import com.atg.autonexo.backend.payment.domain.model.commands.CancelPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.CompletePaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.FailPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.commands.RefundPaymentCommand;
import com.atg.autonexo.backend.payment.domain.model.queries.GetPaymentByIdQuery;
import com.atg.autonexo.backend.payment.domain.model.queries.GetWorkshopPaymentsQuery;
import com.atg.autonexo.backend.payment.domain.services.PaymentCommandService;
import com.atg.autonexo.backend.payment.domain.services.PaymentQueryService;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.CreateSubscriptionPaymentResource;
import com.atg.autonexo.backend.payment.interfaces.rest.resources.PaymentResource;
import com.atg.autonexo.backend.payment.interfaces.rest.transform.CreateSubscriptionPaymentCommandFromResourceAssembler;
import com.atg.autonexo.backend.payment.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for payment operations.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {
    
    private final PaymentCommandService paymentCommandService;
    private final PaymentQueryService paymentQueryService;
    
    /**
     * Create a new subscription payment.
     */
    @PostMapping("/subscriptions")
    @PreAuthorize("hasRole('WORKSHOP_MANAGER')")
    @Operation(summary = "Create subscription payment", description = "Create a new payment for a workshop subscription")
    public ResponseEntity<PaymentResource> createSubscriptionPayment(
            @RequestBody CreateSubscriptionPaymentResource resource) {
        var command = CreateSubscriptionPaymentCommandFromResourceAssembler.toCommandFromResource(resource);
        var payment = paymentCommandService.handle(command);
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return new ResponseEntity<>(paymentResource, HttpStatus.CREATED);
    }
    
    /**
     * Get payment by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('WORKSHOP_MANAGER', 'ADMIN')")
    @Operation(summary = "Get payment by ID", description = "Get detailed information about a payment")
    public ResponseEntity<PaymentResource> getPaymentById(@PathVariable Long id) {
        var query = new GetPaymentByIdQuery(id);
        var payment = paymentQueryService.handle(query);
        
        if (payment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment.get());
        return ResponseEntity.ok(paymentResource);
    }
    
    /**
     * Get payments for current workshop.
     */
    @GetMapping("/my-payments")
    @PreAuthorize("hasRole('WORKSHOP_MANAGER')")
    @Operation(summary = "Get my payments", description = "Get all payments for the current workshop")
    public ResponseEntity<Page<PaymentResource>> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long workshopId = getCurrentWorkshopId();
        var query = new GetWorkshopPaymentsQuery(workshopId, page, size);
        var payments = paymentQueryService.handle(query);
        var paymentResources = payments.map(PaymentResourceFromEntityAssembler::toResourceFromEntity);
        return ResponseEntity.ok(paymentResources);
    }
    
    /**
     * Complete a pending payment (simulated payment processing).
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('WORKSHOP_MANAGER', 'ADMIN')")
    @Operation(summary = "Complete payment", description = "Mark a pending payment as completed (simulated)")
    public ResponseEntity<PaymentResource> completePayment(@PathVariable Long id) {
        var command = new CompletePaymentCommand(id);
        var payment = paymentCommandService.handle(command);
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return ResponseEntity.ok(paymentResource);
    }
    
    /**
     * Mark payment as failed (for testing purposes).
     */
    @PostMapping("/{id}/fail")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Fail payment", description = "Mark a payment as failed (for testing)")
    public ResponseEntity<PaymentResource> failPayment(@PathVariable Long id) {
        var command = new FailPaymentCommand(id);
        var payment = paymentCommandService.handle(command);
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return ResponseEntity.ok(paymentResource);
    }
    
    /**
     * Refund a completed payment.
     */
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('WORKSHOP_MANAGER', 'ADMIN')")
    @Operation(summary = "Refund payment", description = "Refund a completed payment")
    public ResponseEntity<PaymentResource> refundPayment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        var command = new RefundPaymentCommand(id, reason);
        var payment = paymentCommandService.handle(command);
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return ResponseEntity.ok(paymentResource);
    }
    
    /**
     * Cancel a pending payment.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('WORKSHOP_MANAGER', 'ADMIN')")
    @Operation(summary = "Cancel payment", description = "Cancel a pending payment")
    public ResponseEntity<PaymentResource> cancelPayment(@PathVariable Long id) {
        var command = new CancelPaymentCommand(id);
        var payment = paymentCommandService.handle(command);
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return ResponseEntity.ok(paymentResource);
    }
    
    /**
     * Get current workshop ID from context.
     */
    private Long getCurrentWorkshopId() {
        return WorkshopContext.getCurrentWorkshopIdAsLong();
    }
}

