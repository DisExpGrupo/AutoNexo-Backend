package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to create and send a new invitation for a staff member.
 * Simple invitation to join the workshop without defining internal roles.
 */
public record CreateInvitationCommand(
    Long workshopId,
    String email,
    String message,
    Integer validityDays
) {
    public CreateInvitationCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        if (validityDays != null && validityDays <= 0) {
            throw new IllegalArgumentException("Validity days must be positive.");
        }
    }
}
