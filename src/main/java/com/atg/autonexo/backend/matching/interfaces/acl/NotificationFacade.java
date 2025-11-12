package com.atg.autonexo.backend.matching.interfaces.acl;

/**
 * Anti-Corruption Layer facade for Notifications Bounded Context.
 * Provides notification capabilities for Matching & Booking events.
 */
public interface NotificationFacade {
    
    /**
     * Notifies user when they receive a new offer.
     */
    void notifyOfferReceived(Long serviceRequestId, Long offerId, String userEmail);
    
    /**
     * Notifies workshop when their offer is accepted.
     */
    void notifyOfferAccepted(Long offerId, String workshopEmail);
    
    /**
     * Notifies workshop when their offer is rejected.
     */
    void notifyOfferRejected(Long offerId, String workshopEmail);
    
    /**
     * Notifies user when service is completed.
     */
    void notifyServiceCompleted(Long serviceBookingId, String userEmail);
    
    /**
     * Notifies workshop when user confirms pickup.
     */
    void notifyPickupConfirmed(Long serviceBookingId, String workshopEmail);
    
    /**
     * Notifies both user and workshop about upcoming service (24h reminder).
     */
    void notifyUpcomingService(Long serviceBookingId, String userEmail, String workshopEmail);
}

