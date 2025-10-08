package com.atg.autonexo.backend.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Value Object for phone numbers.
 * Ensures immutability and validation.
 */
@Embeddable
public record Phone(@Column(name = "phone_number") String number) {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9 .\\-()]{7,20}$"
    );

    public Phone {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank.");
        }
        if (!PHONE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + number);
        }
    }

    @Override
    public String toString() {
        return number;
    }
}
