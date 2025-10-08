package com.atg.autonexo.backend.workshop.application.internal.commandservices;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Email;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
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
    
    public InvitationCommandServiceImpl(
            InvitationRepository invitationRepository,
            WorkshopRepository workshopRepository,
            NotificationService notificationService) {
        this.invitationRepository = invitationRepository;
        this.workshopRepository = workshopRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    public Invitation handle(CreateInvitationCommand command) {
        LOGGER.info("Creating invitation for workshop ID: {}", command.workshopId());
        
        try {
            // Verify workshop exists
            Workshop workshop = workshopRepository.findById(command.workshopId())
                .orElseThrow(() -> new WorkshopNotFoundException(command.workshopId()));
            
            // Generate unique invitation code
            String code = generateUniqueCode();
            
            // Calculate expiration date
            int validityDays = command.validityDays() != null ? command.validityDays() : DEFAULT_VALIDITY_DAYS;
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(validityDays);
            
            // Create invitation
            Email email = command.email() != null ? new Email(command.email()) : null;
            Invitation invitation = new Invitation(
                new InvitationCode(code),
                expiresAt,
                email,
                new WorkshopId(command.workshopId()),
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
            
        } catch (WorkshopNotFoundException e) {
            LOGGER.error("Workshop not found: {}", command.workshopId());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error creating invitation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create invitation", e);
        }
    }
    
    @Override
    public StaffMember handle(AcceptInvitationCommand command) {
        LOGGER.info("Accepting invitation with code: {}", command.invitationCode());
        
        try {
            // Find invitation
            Invitation invitation = invitationRepository.findByCode(command.invitationCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation code"));
            
            // Validate invitation can be used
            if (!invitation.canBeUsed()) {
                if (invitation.isExpired()) {
                    throw new IllegalStateException("Invitation has expired");
                }
                throw new IllegalStateException("Invitation has already been used");
            }
            
            // Find workshop
            Workshop workshop = workshopRepository.findById(invitation.getWorkshopId().id())
                .orElseThrow(() -> new WorkshopNotFoundException(invitation.getWorkshopId().id()));
            
            // Create staff member
            StaffMember staffMember = new StaffMember(
                new UserId(command.userId()),
                null // No primary location assigned yet
            );
            
            workshop.addStaffMember(staffMember);
            invitation.markAsUsed();
            
            workshopRepository.save(workshop);
            invitationRepository.save(invitation);
            
            LOGGER.info("Invitation accepted, staff member added to workshop: {}", workshop.getId());
            return staffMember;
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOGGER.warn("Failed to accept invitation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error accepting invitation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to accept invitation", e);
        }
    }
    
    /**
     * Generates a unique 8-character invitation code
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
        } while (invitationRepository.existsByCode(code) && attempts < 20);
        
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

