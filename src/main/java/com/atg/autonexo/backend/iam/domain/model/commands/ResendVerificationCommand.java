package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to resend email verification token.
 */
public record ResendVerificationCommand(String email) {
    public ResendVerificationCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}


