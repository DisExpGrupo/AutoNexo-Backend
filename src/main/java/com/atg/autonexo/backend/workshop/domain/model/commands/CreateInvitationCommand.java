package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to create and send a new invitation for a staff member.
 * Simple invitation to join the workshop without defining internal roles.
 * Note: workshopId is obtained from WorkshopContext (JWT token)
 */
public record CreateInvitationCommand(
    String email,
    String message,
    Integer validityDays
) {
    public CreateInvitationCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        if (validityDays != null && validityDays <= 0) {
            throw new IllegalArgumentException("Validity days must be positive.");
        }
    }
}
