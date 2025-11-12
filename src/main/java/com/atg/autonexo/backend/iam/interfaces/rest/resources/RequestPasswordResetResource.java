package com.atg.autonexo.backend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for requesting a password reset.
 */
public record RequestPasswordResetResource(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email
) {}


