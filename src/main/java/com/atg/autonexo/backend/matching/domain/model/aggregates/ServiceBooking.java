package com.atg.autonexo.backend.matching.domain.model.aggregates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Money;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceBookingStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

/**
 * ServiceBooking aggregate root.
 * Represents a scheduled service after an offer has been accepted.
 */
@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "service_bookings")
public class ServiceBooking extends AuditableAbstractAggregateRoot<ServiceBooking> {
    
    @Column(nullable = false)
    private Long serviceRequestId;
    
    @Column(nullable = false)
    private Long offerId;
    
    @Embedded
    private UserId userId;
    
    @Column(nullable = false)
    private Long vehicleId;
    
    @Embedded
    private WorkshopId workshopId;
    
    @Column
    private LocalDateTime scheduledDate;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "proposed_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "proposed_price_currency"))
    })
    private Money proposedPrice;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "final_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "final_price_currency"))
    })
    private Money finalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBookingStatus status;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @jakarta.persistence.CollectionTable(name = "service_booking_services", joinColumns = @JoinColumn(name = "service_booking_id"))
    @Column(name = "service")
    @Enumerated(EnumType.STRING)
    private List<ServiceCatalog> servicesToPerform = new ArrayList<>();
    
    @Column(length = 1000)
    private String description;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private LocalDateTime pickedUpAt;
    
    @Column
    private LocalDateTime cancelledAt;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cancelled_by_user_id"))
    })
    private UserId cancelledBy;
    
    @Column(length = 500)
    private String cancellationReason;
    
    protected ServiceBooking() {}
    
    /**
     * Creates a new service booking from an accepted offer.
     */
    public ServiceBooking(Long serviceRequestId, Long offerId, UserId userId, Long vehicleId,
                         WorkshopId workshopId, LocalDateTime proposedDate, Money proposedPrice,
                         List<ServiceCatalog> servicesToPerform, String description) {
        if (serviceRequestId == null || serviceRequestId <= 0) {
            throw new IllegalArgumentException("ServiceRequestId must be valid");
        }
        if (offerId == null || offerId <= 0) {
            throw new IllegalArgumentException("OfferId must be valid");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        if (proposedPrice == null) {
            throw new IllegalArgumentException("ProposedPrice cannot be null");
        }
        if (servicesToPerform == null || servicesToPerform.isEmpty()) {
            throw new IllegalArgumentException("ServicesToPerform cannot be null or empty");
        }
        
        this.serviceRequestId = serviceRequestId;
        this.offerId = offerId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.workshopId = workshopId;
        this.scheduledDate = proposedDate;
        this.proposedPrice = proposedPrice;
        this.finalPrice = proposedPrice; // Initially same as proposed
        this.servicesToPerform = new ArrayList<>(servicesToPerform);
        this.description = description;
        this.status = ServiceBookingStatus.PENDING_SCHEDULE;
    }
    
    /**
     * Confirms the scheduled date/time.
     */
    public void confirmSchedule(LocalDateTime scheduledDate) {
        if (scheduledDate == null) {
            throw new IllegalArgumentException("ScheduledDate cannot be null");
        }
        if (status != ServiceBookingStatus.PENDING_SCHEDULE) {
            throw new IllegalStateException("Can only confirm schedule when status is PENDING_SCHEDULE");
        }
        this.scheduledDate = scheduledDate;
        this.status = ServiceBookingStatus.SCHEDULED;
    }
    
    /**
     * Proposes a schedule change (mediación).
     */
    public void proposeScheduleChange(LocalDateTime newScheduledDate) {
        if (newScheduledDate == null) {
            throw new IllegalArgumentException("NewScheduledDate cannot be null");
        }
        if (status != ServiceBookingStatus.PENDING_SCHEDULE && status != ServiceBookingStatus.SCHEDULED) {
            throw new IllegalStateException("Can only propose schedule change when status is PENDING_SCHEDULE or SCHEDULED");
        }
        this.scheduledDate = newScheduledDate;
        this.status = ServiceBookingStatus.PENDING_SCHEDULE; // Back to pending for confirmation
    }
    
    /**
     * Marks the service as completed by the workshop.
     */
    public void markAsCompleted() {
        if (status != ServiceBookingStatus.SCHEDULED && status != ServiceBookingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Can only mark as completed when status is SCHEDULED or IN_PROGRESS");
        }
        this.status = ServiceBookingStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Transitions to PENDING_PICKUP after completion.
     */
    public void transitionToPendingPickup() {
        if (status != ServiceBookingStatus.COMPLETED) {
            throw new IllegalStateException("Can only transition to PENDING_PICKUP when status is COMPLETED");
        }
        this.status = ServiceBookingStatus.PENDING_PICKUP;
    }
    
    /**
     * Confirms pickup by the user.
     */
    public void confirmPickup() {
        if (status != ServiceBookingStatus.PENDING_PICKUP) {
            throw new IllegalStateException("Can only confirm pickup when status is PENDING_PICKUP");
        }
        this.status = ServiceBookingStatus.PICKED_UP;
        this.pickedUpAt = LocalDateTime.now();
    }
    
    /**
     * Cancels the service booking.
     */
    public void cancel(UserId cancelledBy, String cancellationReason) {
        if (cancelledBy == null) {
            throw new IllegalArgumentException("CancelledBy cannot be null");
        }
        if (status == ServiceBookingStatus.PICKED_UP) {
            throw new IllegalStateException("Cannot cancel a service that has been picked up");
        }
        this.status = ServiceBookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = cancellationReason;
    }
    
    /**
     * Updates the final price (if different from proposed).
     */
    public void updateFinalPrice(Money finalPrice) {
        if (finalPrice == null) {
            throw new IllegalArgumentException("FinalPrice cannot be null");
        }
        this.finalPrice = finalPrice;
    }
    
    /**
     * Checks if the booking can be cancelled.
     */
    public boolean canBeCancelled() {
        return status != ServiceBookingStatus.PICKED_UP;
    }
}

