package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for accepting an invitation
 */
public record AcceptInvitationResource(
    @NotBlank(message = "Invitation code is required")
    String invitationCode,
    
    @NotNull(message = "User ID is required")
    Long userId
) {
}

