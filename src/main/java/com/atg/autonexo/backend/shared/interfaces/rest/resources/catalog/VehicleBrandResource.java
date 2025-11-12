package com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog;

/**
 * Resource representing a vehicle brand.
 */
public record VehicleBrandResource(
    Long id,
    String name,
    String logoUrl,
    String country,
    boolean isActive,
    boolean popular
) {}

