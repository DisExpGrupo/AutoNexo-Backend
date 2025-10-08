package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to add a new location to a workshop
 */
public record AddLocationCommand(
    Long workshopId,
    String street,
    String city,
    String state,
    String zip,
    String country,
    Double latitude,
    Double longitude
) {
    public AddLocationCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank.");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank.");
        }
        if (zip == null || zip.isBlank()) {
            throw new IllegalArgumentException("Zip cannot be null or blank.");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank.");
        }
    }
}

