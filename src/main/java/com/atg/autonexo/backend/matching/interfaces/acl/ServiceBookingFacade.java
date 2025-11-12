package com.atg.autonexo.backend.matching.interfaces.acl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceBookingStatus;
import com.atg.autonexo.backend.matching.infrastructure.persistence.jpa.repositories.ServiceBookingRepository;

import lombok.RequiredArgsConstructor;

/**
 * ACL Facade for ServiceBooking operations exposed to other bounded contexts.
 * Specifically for Trust & Reputation context to validate review creation.
 */
@Service
@RequiredArgsConstructor
public class ServiceBookingFacade {
    
    private final ServiceBookingRepository serviceBookingRepository;
    
    /**
     * Get service booking information for review validation.
     * 
     * @param serviceBookingId the service booking ID
     * @return service booking info if found
     */
    public Optional<ServiceBookingInfo> getServiceBookingInfo(Long serviceBookingId) {
        return serviceBookingRepository.findById(serviceBookingId)
            .map(sb -> new ServiceBookingInfo(
                sb.getId(),
                sb.getUserId().id(),
                sb.getWorkshopId().id(),
                sb.getStatus(),
                sb.getCompletedAt(),
                sb.getPickedUpAt(),
                sb.getCancelledAt()
            ));
    }
    
    /**
     * Validate if a user can review a service booking.
     * 
     * @param serviceBookingId the service booking ID
     * @param userId the user ID attempting to review
     * @return true if user can review (is participant and service is completed/cancelled)
     */
    public boolean validateUserCanReview(Long serviceBookingId, Long userId) {
        var info = getServiceBookingInfo(serviceBookingId);
        if (info.isEmpty()) {
            return false;
        }
        
        var booking = info.get();
        
        // User must be the car owner (not the workshop)
        boolean isCarOwner = booking.userId().equals(userId);
        
        // Service must be completed or cancelled
        boolean isFinished = booking.status() == ServiceBookingStatus.PICKED_UP ||
                            booking.status() == ServiceBookingStatus.CANCELLED;
        
        return isCarOwner && isFinished;
    }
    
    /**
     * Validate if a workshop can review a service booking.
     * 
     * @param serviceBookingId the service booking ID
     * @param workshopId the workshop ID attempting to review
     * @return true if workshop can review
     */
    public boolean validateWorkshopCanReview(Long serviceBookingId, Long workshopId) {
        var info = getServiceBookingInfo(serviceBookingId);
        if (info.isEmpty()) {
            return false;
        }
        
        var booking = info.get();
        
        // Workshop must be the service provider
        boolean isServiceProvider = booking.workshopId().equals(workshopId);
        
        // Service must be completed or cancelled
        boolean isFinished = booking.status() == ServiceBookingStatus.PICKED_UP ||
                            booking.status() == ServiceBookingStatus.CANCELLED;
        
        return isServiceProvider && isFinished;
    }
    
    /**
     * Record object for service booking information.
     */
    public record ServiceBookingInfo(
        Long id,
        Long userId,
        Long workshopId,
        ServiceBookingStatus status,
        LocalDateTime completedAt,
        LocalDateTime pickedUpAt,
        LocalDateTime cancelledAt
    ) {
        /**
         * Get the completion date (either picked up or cancelled).
         */
        public LocalDateTime getCompletionDate() {
            if (pickedUpAt != null) {
                return pickedUpAt;
            }
            if (cancelledAt != null) {
                return cancelledAt;
            }
            if (completedAt != null) {
                return completedAt;
            }
            return null;
        }
        
        /**
         * Check if service is finished.
         */
        public boolean isFinished() {
            return status == ServiceBookingStatus.PICKED_UP ||
                   status == ServiceBookingStatus.CANCELLED;
        }
    }
}

