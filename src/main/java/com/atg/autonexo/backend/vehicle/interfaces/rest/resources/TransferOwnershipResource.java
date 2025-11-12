package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for transferring vehicle ownership.
 */
public record TransferOwnershipResource(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String newOwnerEmail
) {}

