package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to deactivate user account (soft delete).
 */
public record DeactivateUserCommand(Long userId) {
    public DeactivateUserCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}


