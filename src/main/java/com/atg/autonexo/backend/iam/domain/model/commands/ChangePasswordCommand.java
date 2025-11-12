package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to change user password when authenticated.
 */
public record ChangePasswordCommand(
    Long userId,
    String currentPassword,
    String newPassword
) {
    public ChangePasswordCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Current password cannot be null or empty");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }
    }
}


