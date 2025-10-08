package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Resource for invitation information responses
 */
public record InvitationResource(
    Long id,
    String invitationCode,
    String email,
    Long workshopId,
    String message,
    LocalDateTime expiresAt,
    boolean used,
    boolean expired,
    boolean canBeUsed,
    Date createdAt
) {
}

