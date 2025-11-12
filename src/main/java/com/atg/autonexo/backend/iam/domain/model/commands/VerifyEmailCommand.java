package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to verify email with a token.
 */
public record VerifyEmailCommand(String token) {
    public VerifyEmailCommand {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
    }
}


