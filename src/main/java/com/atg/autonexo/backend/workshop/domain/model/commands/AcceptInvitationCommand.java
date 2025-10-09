package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to accept an invitation and become a staff member
 */
public record AcceptInvitationCommand(
    String invitationCode,
    String email
) {
    public AcceptInvitationCommand {
        if (invitationCode == null || invitationCode.isBlank()) {
            throw new IllegalArgumentException("Invitation code cannot be null or blank.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
    }
}

