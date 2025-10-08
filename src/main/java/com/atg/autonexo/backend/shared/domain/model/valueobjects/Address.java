package com.atg.autonexo.backend.shared.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String street, String city, String state, String zip, String country) {
    public Address {
        if (street == null || street.isEmpty()) {
            throw new IllegalArgumentException("Street cannot be null or empty.");
        }
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty.");
        }
        if (state == null || state.isEmpty()) {
            throw new IllegalArgumentException("State cannot be null or empty.");
        }
        if (zip == null || zip.isEmpty()) {
            throw new IllegalArgumentException("Zip cannot be null or empty.");
        }
        if (country == null || country.isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty.");
        }
    }
    
}
