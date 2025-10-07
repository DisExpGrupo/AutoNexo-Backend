package com.atg.autonexo.backend.iam.domain.model.commands;

/**
 * Command to register a new user in the system.
 * It does not contain an ID, as the ID is generated upon creation.
 */
public record SignUpCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber
) {
    public SignUpCommand {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty.");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty.");
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
    }
}
