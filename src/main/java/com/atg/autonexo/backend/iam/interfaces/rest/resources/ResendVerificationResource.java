package com.atg.autonexo.backend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for resending email verification.
 */
public record ResendVerificationResource(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email
) {}


