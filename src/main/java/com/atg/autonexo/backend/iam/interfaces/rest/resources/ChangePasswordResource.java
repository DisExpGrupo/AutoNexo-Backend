package com.atg.autonexo.backend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for changing password.
 */
public record ChangePasswordResource(
    @NotBlank(message = "Current password is required")
    String currentPassword,
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    String newPassword
) {}


