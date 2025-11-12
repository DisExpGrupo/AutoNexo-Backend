package com.atg.autonexo.backend.workshop.application.acl;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.entities.WorkshopReference;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.WorkshopReferenceRepository;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.InvitationRepository;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;
import com.atg.autonexo.backend.workshop.interfaces.acl.WorkshopContextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of Workshop Context Facade (ACL)
 * <p>
 * This service acts as an Anti-Corruption Layer between IAM and Workshop bounded contexts.
 * It handles cross-context operations while maintaining domain boundaries.
 * </p>
 */
@Service
@Transactional
public class WorkshopContextFacadeImpl implements WorkshopContextFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkshopContextFacadeImpl.class);
    
    private final InvitationRepository invitationRepository;
    private final WorkshopRepository workshopRepository;
    private final UserRepository userRepository;
    private final WorkshopReferenceRepository workshopReferenceRepository;
    
    public WorkshopContextFacadeImpl(
            InvitationRepository invitationRepository,
            WorkshopRepository workshopRepository,
            UserRepository userRepository,
            WorkshopReferenceRepository workshopReferenceRepository) {
        this.invitationRepository = invitationRepository;
        this.workshopRepository = workshopRepository;
        this.userRepository = userRepository;
        this.workshopReferenceRepository = workshopReferenceRepository;
    }
    
    @Override
    public Long processInvitationForNewUser(String invitationCode, String userEmail, Long userId) {
        LOGGER.info("Processing invitation {} for new user: {} (ID: {})", invitationCode, userEmail, userId);
        
        try {
            // Load all invitations and filter using domain logic
            Invitation invitation = invitationRepository.findAll().stream()
                .filter(inv -> inv.matchesCodeAndEmail(invitationCode, userEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Invalid invitation code or email. The invitation may not exist or was sent to a different email address."));
            
            // Validate invitation can be used
            if (!invitation.canBeUsed()) {
                if (invitation.isExpired()) {
                    throw new IllegalStateException("Invitation has expired");
                }
                throw new IllegalStateException("Invitation has already been used");
            }
            
            // Get user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            
            // Check if user already has a workshop reference
            if (user.getWorkshopReference() != null) {
                throw new IllegalStateException("User is already associated with another workshop");
            }
            
            // Find workshop
            Long workshopId = invitation.getWorkshopId().id();
            Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            // Create or get workshop reference using domain logic
            WorkshopReference workshopReference = workshopReferenceRepository.findAll().stream()
                .filter(ref -> ref.isForWorkshop(workshopId))
                .findFirst()
                .orElseGet(() -> {
                    WorkshopReference newRef = new WorkshopReference(workshopId);
                    return workshopReferenceRepository.save(newRef);
                });
            
            // Associate user with workshop in IAM context
            user.setWorkshopReference(workshopReference);
            userRepository.save(user);
            
            // Create staff member in workshop context
            StaffMember staffMember = new StaffMember(
                new UserId(userId),
                null // No primary location assigned yet
            );
            
            workshop.addStaffMember(staffMember);
            invitation.markAsUsed();
            
            workshopRepository.save(workshop);
            invitationRepository.save(invitation);
            
            LOGGER.info("Successfully processed invitation. User {} added to workshop {}", userId, workshopId);
            return workshopId;
            
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid invitation parameters: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            LOGGER.warn("Invalid invitation state: {}", e.getMessage());
            throw e;
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error processing invitation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process invitation", e);
        }
    }
    
    @Override
    public void associateUserWithWorkshop(Long userId, Long workshopId) {
        LOGGER.info("Associating user {} with workshop {}", userId, workshopId);
        
        try {
            // Get user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            
            // Check if user already has a workshop reference
            if (user.getWorkshopReference() != null) {
                throw new IllegalArgumentException("User is already associated with another workshop");
            }
            
            // Verify workshop exists
            workshopRepository.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            // Create or get workshop reference using domain logic
            WorkshopReference workshopReference = workshopReferenceRepository.findAll().stream()
                .filter(ref -> ref.isForWorkshop(workshopId))
                .findFirst()
                .orElseGet(() -> {
                    WorkshopReference newRef = new WorkshopReference(workshopId);
                    return workshopReferenceRepository.save(newRef);
                });
            
            // Associate user with workshop
            user.setWorkshopReference(workshopReference);
            userRepository.save(user);
            
            LOGGER.info("Successfully associated user {} with workshop {}", userId, workshopId);
            
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to associate user with workshop: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error associating user with workshop: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to associate user with workshop", e);
        }
    }
    
    @Override
    public boolean userHasWorkshop(Long userId) {
        try {
            return userRepository.findById(userId)
                .map(user -> user.getWorkshopReference() != null)
                .orElse(false);
        } catch (Exception e) {
            LOGGER.error("Error checking if user has workshop: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Long getWorkshopIdForUser(Long userId) {
        try {
            return userRepository.findById(userId)
                .map(User::getWorkshopReference)
                .map(WorkshopReference::getWorkshopId)
                .map(WorkshopId::id)
                .orElse(null);
        } catch (Exception e) {
            LOGGER.error("Error getting workshop ID for user: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public void updateWorkshopTrustScore(Long workshopId, Float trustScore) {
        LOGGER.info("Updating trust score for workshop {} to {}", workshopId, trustScore);
        
        try {
            Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            workshop.setTrustScore(trustScore);
            workshopRepository.save(workshop);
            
            LOGGER.info("Successfully updated trust score for workshop {}", workshopId);
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Workshop not found when updating trust score: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating workshop trust score: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update workshop trust score", e);
        }
    }
    
    @Override
    public boolean workshopExists(Long workshopId) {
        try {
            return workshopRepository.findById(workshopId)
                .map(Workshop::isActive)
                .orElse(false);
        } catch (Exception e) {
            LOGGER.error("Error checking if workshop exists: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public void updateSubscription(
            Long workshopId,
            com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier tier,
            com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus status,
            java.time.LocalDate expiresAt) {
        LOGGER.info("Updating subscription for workshop {} to tier {}, status {}, expires at {}", 
            workshopId, tier, status, expiresAt);
        
        try {
            Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            workshop.setSubscriptionTier(tier);
            workshop.setSubscriptionStatus(status);
            // Convert LocalDate to LocalDateTime (end of day)
            workshop.setSubscriptionExpiresAt(expiresAt.atTime(23, 59, 59));
            workshopRepository.save(workshop);
            
            LOGGER.info("Successfully updated subscription for workshop {}", workshopId);
        } catch (WorkshopNotFoundException e) {
            LOGGER.warn("Workshop not found when updating subscription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating workshop subscription: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update workshop subscription", e);
        }
    }
}
