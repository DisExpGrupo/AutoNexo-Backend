package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record InvitationCode(@Column(name="invitation_code") String value) {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]{8}$");

    public InvitationCode {
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid invitation code");
        }
    }
    
}
