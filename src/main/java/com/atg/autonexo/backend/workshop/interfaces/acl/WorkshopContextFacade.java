package com.atg.autonexo.backend.workshop.interfaces.acl;

/**
 * Anti-Corruption Layer (ACL) Facade for Workshop Bounded Context
 * <p>
 * This interface provides a clean boundary between the IAM and Workshop bounded contexts.
 * It exposes only the necessary operations that other bounded contexts can use,
 * protecting the internal domain model of the Workshop context.
 * </p>
 */
public interface WorkshopContextFacade {
    
    /**
     * Process an invitation acceptance for a user registering via IAM
     * This method is called during user signup when an invitation code is provided
     * 
     * @param invitationCode the invitation code
     * @param userEmail the email of the user accepting the invitation
     * @param userId the ID of the newly created user in IAM
     * @return the workshop ID the user was added to, or null if invitation processing failed
     * @throws IllegalArgumentException if invitation is invalid or email doesn't match
     * @throws IllegalStateException if invitation has expired or been used
     */
    Long processInvitationForNewUser(String invitationCode, String userEmail, Long userId);
    
    /**
     * Associate a user with a workshop when the workshop is created
     * This is used when a WORKSHOP_MANAGER creates their workshop
     * 
     * @param userId the ID of the user (workshop owner)
     * @param workshopId the ID of the newly created workshop
     * @throws IllegalArgumentException if user already has a workshop
     */
    void associateUserWithWorkshop(Long userId, Long workshopId);
    
    /**
     * Check if a user already has a workshop associated
     * 
     * @param userId the ID of the user to check
     * @return true if user already has a workshop, false otherwise
     */
    boolean userHasWorkshop(Long userId);
    
    /**
     * Get the workshop ID associated with a user
     * 
     * @param userId the ID of the user
     * @return the workshop ID, or null if user has no workshop
     */
    Long getWorkshopIdForUser(Long userId);
    
    /**
     * Update the trust score for a workshop
     * Called by Trust & Reputation context after calculating the score
     * 
     * @param workshopId the ID of the workshop
     * @param trustScore the new trust score to set
     */
    void updateWorkshopTrustScore(Long workshopId, Float trustScore);
    
    /**
     * Check if a workshop exists
     * 
     * @param workshopId the ID of the workshop
     * @return true if workshop exists and is active
     */
    boolean workshopExists(Long workshopId);
    
    /**
     * Update workshop subscription after successful payment
     * Called by Payment context after a successful subscription payment
     * 
     * @param workshopId the ID of the workshop
     * @param tier the new subscription tier
     * @param status the subscription status
     * @param expiresAt the new expiration date
     */
    void updateSubscription(
        Long workshopId,
        com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier tier,
        com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus status,
        java.time.LocalDate expiresAt
    );
}
