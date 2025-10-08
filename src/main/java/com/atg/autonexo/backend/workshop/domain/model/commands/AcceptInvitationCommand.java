package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to accept an invitation and become a staff member
 */
public record AcceptInvitationCommand(
    String invitationCode,
    Long userId
) {
    public AcceptInvitationCommand {
        if (invitationCode == null || invitationCode.isBlank()) {
            throw new IllegalArgumentException("Invitation code cannot be null or blank.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative.");
        }
    }
}

