package com.atg.autonexo.backend.workshop.domain.model.queries;

/**
 * Query to get an invitation by code
 */
public record GetInvitationByCodeQuery(String code) {
    public GetInvitationByCodeQuery {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or blank.");
        }
    }
}

