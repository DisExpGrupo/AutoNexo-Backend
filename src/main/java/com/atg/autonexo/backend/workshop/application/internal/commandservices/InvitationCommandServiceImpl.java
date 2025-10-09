package com.atg.autonexo.backend.workshop.application.internal.commandservices;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.entities.WorkshopReference;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.WorkshopReferenceRepository;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Email;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopContextNotFoundException;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.commands.AcceptInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.CreateInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.InvitationCode;
import com.atg.autonexo.backend.workshop.domain.services.InvitationCommandService;
import com.atg.autonexo.backend.workshop.domain.services.NotificationService;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.InvitationRepository;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Implementation of Invitation Command Service.
 * Handles invitation creation and acceptance flow.
 */
@Service
@Transactional
public class InvitationCommandServiceImpl implements InvitationCommandService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationCommandServiceImpl.class);
    private static final int DEFAULT_VALIDITY_DAYS = 7;
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();
    
    private final InvitationRepository invitationRepository;
    private final WorkshopRepository workshopRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final WorkshopReferenceRepository workshopReferenceRepository;
    
    public InvitationCommandServiceImpl(
            InvitationRepository invitationRepository,
            WorkshopRepository workshopRepository,
            NotificationService notificationService,
            UserRepository userRepository,
            WorkshopReferenceRepository workshopReferenceRepository) {
        this.invitationRepository = invitationRepository;
        this.workshopRepository = workshopRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.workshopReferenceRepository = workshopReferenceRepository;
    }
    
    @Override
    public Invitation handle(CreateInvitationCommand command) {
        // Get workshop ID from context
        if (!WorkshopContext.hasWorkshopContext()) {
            throw new WorkshopContextNotFoundException();
        }
        
        Long workshopId = WorkshopContext.getCurrentWorkshopIdAsLong();
        LOGGER.info("Creating invitation for workshop ID: {}", workshopId);
        
        try {
            // Verify workshop exists
            Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException(workshopId));
            
            // Generate unique invitation code
            String code = generateUniqueCode();
            
            // Calculate expiration date
            Integer validityDaysInput = command.validityDays();
            int validityDays = (validityDaysInput != null) ? validityDaysInput : DEFAULT_VALIDITY_DAYS;
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(validityDays);
            
            // Create invitation
            Email email = command.email() != null ? new Email(command.email()) : null;
            Invitation invitation = new Invitation(
                new InvitationCode(code),
                expiresAt,
                email,
                new WorkshopId(workshopId),
                command.message()
            );
            
            Invitation savedInvitation = invitationRepository.save(invitation);
            LOGGER.info("Invitation created successfully with code: {}", code);
            
            // Send notification (async in production)
            if (email != null) {
                try {
                    notificationService.sendInvitationEmail(
                        command.email(),
                        code,
                        workshop.getName()
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to send invitation email", e);
                    // Don't fail the invitation creation if email fails
                }
            }
            
            return savedInvitation;
            
        } catch (WorkshopNotFoundException | WorkshopContextNotFoundException e) {
            LOGGER.error("Workshop not found: {}", workshopId);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error creating invitation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create invitation", e);
        }
    }
    
    @Override
    public StaffMember handle(AcceptInvitationCommand command) {
        LOGGER.info("Accepting invitation with code: {} for email: {}", command.invitationCode(), command.email());
        
        try {
            // Find invitation using domain logic
            Invitation invitation = invitationRepository.findAll().stream()
                .filter(inv -> inv.matchesCodeAndEmail(command.invitationCode(), command.email()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Invalid invitation code or email. Please verify your invitation details."));
            
            // Validate invitation can be used
            if (!invitation.canBeUsed()) {
                if (invitation.isExpired()) {
                    throw new IllegalStateException("Invitation has expired");
                }
                throw new IllegalStateException("Invitation has already been used");
            }
            
            // Find user by email
            User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException(
                    "User not found with email: " + command.email()));
            
            // Check if user already has a workshop reference
            if (user.getWorkshopReference() != null) {
                throw new IllegalStateException(
                    "User is already associated with another workshop");
            }
            
            // Find workshop
            Workshop workshop = workshopRepository.findById(invitation.getWorkshopId().id())
                .orElseThrow(() -> new WorkshopNotFoundException(invitation.getWorkshopId().id()));
            
            // Create or get workshop reference using domain logic
            Long workshopId = invitation.getWorkshopId().id();
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
                new UserId(user.getId()),
                null // No primary location assigned yet
            );
            
            workshop.addStaffMember(staffMember);
            invitation.markAsUsed();
            
            workshopRepository.save(workshop);
            invitationRepository.save(invitation);
            
            LOGGER.info("Invitation accepted, user {} added as staff member to workshop: {}", 
                user.getId(), workshop.getId());
            return staffMember;
            
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
            LOGGER.error("Error accepting invitation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to accept invitation", e);
        }
    }
    
    /**
     * Generates a unique 8-character invitation code
     * Uses domain logic to check for uniqueness
     */
    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        do {
            code = generateRandomCode();
            attempts++;
            if (attempts > 10) {
                // Fallback: add timestamp to ensure uniqueness
                code = generateRandomCode().substring(0, 6) + 
                       String.format("%02d", System.currentTimeMillis() % 100);
            }
            // Check uniqueness using domain logic
            final String currentCode = code;
            boolean exists = invitationRepository.findAll().stream()
                .anyMatch(inv -> inv.hasCode(currentCode));
            if (!exists) {
                break;
            }
        } while (attempts < 20);
        
        return code;
    }
    
    /**
     * Generates a random 8-character alphanumeric code
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return code.toString();
    }
}

