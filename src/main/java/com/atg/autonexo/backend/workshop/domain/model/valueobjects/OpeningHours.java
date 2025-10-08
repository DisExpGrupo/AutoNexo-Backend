package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Embeddable;

/**
 * Value Object representing operating hours for a specific day.
 * Used to define when a workshop location is open.
 */
@Embeddable
public record OpeningHours(
    DayOfWeek dayOfWeek,
    LocalTime opensAt,
    LocalTime closesAt,
    boolean isClosed
) {
    
    public OpeningHours {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week cannot be null.");
        }
        
        if (!isClosed) {
            if (opensAt == null || closesAt == null) {
                throw new IllegalArgumentException("Opening and closing times must be provided when not closed.");
            }
            if (opensAt.isAfter(closesAt) || opensAt.equals(closesAt)) {
                throw new IllegalArgumentException("Opening time must be before closing time.");
            }
        }
    }
    
    /**
     * Creates a closed day
     */
    public static OpeningHours closed(DayOfWeek dayOfWeek) {
        return new OpeningHours(dayOfWeek, null, null, true);
    }
    
    /**
     * Checks if the location is open at a specific time
     */
    public boolean isOpenAt(LocalTime time) {
        if (isClosed) {
            return false;
        }
        return !time.isBefore(opensAt) && time.isBefore(closesAt);
    }
}

