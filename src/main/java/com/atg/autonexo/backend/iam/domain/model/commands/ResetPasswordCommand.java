package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to reset password using a reset token.
 */
public record ResetPasswordCommand(String token, String newPassword) {
    public ResetPasswordCommand {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
}


