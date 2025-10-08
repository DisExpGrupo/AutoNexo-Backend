package com.atg.autonexo.backend.shared.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Value Object representing monetary amounts.
 * Ensures immutability and validation for price-related fields.
 */
@Embeddable
public record Money(BigDecimal amount, String currency) {
    
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank.");
        }
        // Validate currency code (ISO 4217 format)
        if (currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code.");
        }
    }
    
    public Money withAmount(BigDecimal newAmount) {
        return new Money(newAmount, this.currency);
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currencies.");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

