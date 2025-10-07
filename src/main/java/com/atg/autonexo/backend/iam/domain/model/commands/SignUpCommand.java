package com.atg.autonexo.backend.iam.domain.model.commands;

import com.atg.autonexo.backend.iam.domain.model.valueobjects.Roles;

/**
 * Command to register a new user in the system.
 * It does not contain an ID, as the ID is generated upon creation.
 * Users must specify which role they are applying for during registration.
 */
public record SignUpCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        Roles requestedRole
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
        if (requestedRole == null) {
            throw new IllegalArgumentException("Requested role cannot be null.");
        }
    }
}
