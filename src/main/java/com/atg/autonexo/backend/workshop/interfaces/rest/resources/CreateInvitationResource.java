package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a staff invitation
 */
public record CreateInvitationResource(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @Size(max = 500, message = "Message must not exceed 500 characters")
    String message,
    
    @Min(value = 1, message = "Validity days must be at least 1")
    Integer validityDays
) {
}

