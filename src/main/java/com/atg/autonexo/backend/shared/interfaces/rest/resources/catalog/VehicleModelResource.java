package com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog;

/**
 * Resource representing a vehicle model.
 */
public record VehicleModelResource(
    Long id,
    Long brandId,
    String name,
    Integer startYear,
    Integer endYear,
    boolean isActive
) {}

