package com.atg.autonexo.backend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for verifying email with a token.
 */
public record VerifyEmailResource(
    @NotBlank(message = "Token is required")
    String token
) {}


