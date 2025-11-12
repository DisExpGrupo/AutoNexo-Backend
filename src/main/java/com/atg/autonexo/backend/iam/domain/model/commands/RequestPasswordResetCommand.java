package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to request a password reset.
 * The system will generate a token and send it to the user's email.
 */
public record RequestPasswordResetCommand(String email) {
    public RequestPasswordResetCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}


