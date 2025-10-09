package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for accepting an invitation
 */
public record AcceptInvitationResource(
    @NotBlank(message = "Invitation code is required")
    String invitationCode,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email
) {
}

