package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for adding an authorized user to a vehicle.
 */
public record AddAuthorizedUserResource(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email
) {}

