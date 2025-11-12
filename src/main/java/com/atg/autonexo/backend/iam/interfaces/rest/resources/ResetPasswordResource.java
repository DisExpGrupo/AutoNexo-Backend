package com.atg.autonexo.backend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for resetting password with a token.
 */
public record ResetPasswordResource(
    @NotBlank(message = "Token is required")
    String token,
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword
) {}


