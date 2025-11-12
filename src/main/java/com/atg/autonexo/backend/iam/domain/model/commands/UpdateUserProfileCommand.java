package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to update user profile information.
 */
public record UpdateUserProfileCommand(
    Long userId,
    String firstName,
    String lastName,
    String phoneNumber
) {
    public UpdateUserProfileCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (firstName != null && firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (lastName != null && lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
    }
}


