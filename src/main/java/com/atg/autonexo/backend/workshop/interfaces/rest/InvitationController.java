package com.atg.autonexo.backend.workshop.interfaces.rest;

import com.atg.autonexo.backend.shared.infrastructure.multitenancy.WorkshopContext;
import com.atg.autonexo.backend.workshop.application.internal.commandservices.InvitationCommandServiceImpl;
import com.atg.autonexo.backend.workshop.application.internal.queryservices.InvitationQueryServiceImpl;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopContextNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.commands.AcceptInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.CreateInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetInvitationByCodeQuery;
import com.atg.autonexo.backend.workshop.domain.model.queries.GetInvitationsByWorkshopQuery;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.AcceptInvitationResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.CreateInvitationResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.InvitationResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.InvitationResourceFromEntityAssembler;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Invitation REST Controller
 * <p>
 * This controller handles HTTP requests for invitation-related operations including
 * creating invitations, accepting them, and querying invitation status.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationController.class);
    
    private final InvitationCommandServiceImpl invitationCommandService;
    private final InvitationQueryServiceImpl invitationQueryService;
    
    public InvitationController(
            InvitationCommandServiceImpl invitationCommandService,
            InvitationQueryServiceImpl invitationQueryService) {
        this.invitationCommandService = invitationCommandService;
        this.invitationQueryService = invitationQueryService;
    }
    
    /**
     * Create a new invitation for the workshop (uses WorkshopContext from JWT)
     * @param resource the invitation creation data
     * @return ResponseEntity with created invitation
     */
    @PostMapping
    public ResponseEntity<?> createInvitation(@Valid @RequestBody CreateInvitationResource resource) {
        try {
            if (!WorkshopContext.hasWorkshopContext()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Workshop context not found. This operation requires a workshop user.");
            }
            
            Long workshopId = WorkshopContext.getCurrentWorkshopIdAsLong();
            LOGGER.info("Processing create invitation request for workshop ID: {}", workshopId);
            
            CreateInvitationCommand command = new CreateInvitationCommand(
                resource.email(),
                resource.message(),
                resource.validityDays()
            );
            
            Invitation invitation = invitationCommandService.handle(command);
            InvitationResource invitationResource = InvitationResourceFromEntityAssembler
                    .toResourceFromEntity(invitation);
            
            LOGGER.info("Invitation created successfully with code: {}", invitation.getInvitationCode().value());
            return ResponseEntity.status(HttpStatus.CREATED).body(invitationResource);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invitation creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during invitation creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during invitation creation");
        }
    }
    
    /**
     * Accept an invitation and join workshop as staff member
     * Validates both invitation code and user email
     * @param resource the acceptance data (code + email)
     * @return ResponseEntity with success message
     */
    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvitation(@Valid @RequestBody AcceptInvitationResource resource) {
        try {
            LOGGER.info("Processing accept invitation request for code: {} and email: {}", 
                resource.invitationCode(), resource.email());
            
            AcceptInvitationCommand command = new AcceptInvitationCommand(
                resource.invitationCode(),
                resource.email()
            );
            
            StaffMember staffMember = invitationCommandService.handle(command);
            
            LOGGER.info("Invitation accepted successfully, staff member ID: {}", staffMember.getId());
            return ResponseEntity.ok("Invitation accepted successfully. You are now a staff member!");
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOGGER.warn("Invitation acceptance failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during invitation acceptance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during invitation acceptance");
        }
    }
    
    /**
     * Get invitation by code
     * @param code the invitation code
     * @return ResponseEntity with invitation information
     */
    @GetMapping("/{code}")
    public ResponseEntity<?> getInvitationByCode(@PathVariable String code) {
        try {
            LOGGER.debug("Processing get invitation by code request: {}", code);
            
            Optional<Invitation> invitationOptional = invitationQueryService
                    .handle(new GetInvitationByCodeQuery(code));
            
            if (invitationOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Invitation not found");
            }
            
            Invitation invitation = invitationOptional.get();
            InvitationResource invitationResource = InvitationResourceFromEntityAssembler
                    .toResourceFromEntity(invitation);
            
            return ResponseEntity.ok(invitationResource);
            
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving invitation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving invitation");
        }
    }
    
    /**
     * Get all invitations for the workshop (uses WorkshopContext from JWT)
     * @return ResponseEntity with list of invitations
     */
    @GetMapping
    public ResponseEntity<?> getInvitationsByWorkshop() {
        try {
            if (!WorkshopContext.hasWorkshopContext()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Workshop context not found. This operation requires a workshop user.");
            }
            
            Long workshopId = WorkshopContext.getCurrentWorkshopIdAsLong();
            LOGGER.debug("Processing get invitations for workshop request: {}", workshopId);
            
            List<Invitation> invitations = invitationQueryService
                    .handle(new GetInvitationsByWorkshopQuery(workshopId));
            
            List<InvitationResource> invitationResources = invitations.stream()
                    .map(InvitationResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            
            return ResponseEntity.ok(invitationResources);
            
        } catch (WorkshopContextNotFoundException e) {
            LOGGER.warn("Workshop context not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving invitations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving invitations");
        }
    }
}

