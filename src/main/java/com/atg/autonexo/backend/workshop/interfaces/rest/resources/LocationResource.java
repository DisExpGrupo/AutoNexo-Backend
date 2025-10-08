package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import java.util.Date;

/**
 * Resource for location information responses
 */
public record LocationResource(
    Long id,
    String street,
    String city,
    String state,
    String zip,
    String country,
    Double latitude,
    Double longitude,
    boolean active,
    Date createdAt,
    Date updatedAt
) {
}

