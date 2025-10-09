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
}
