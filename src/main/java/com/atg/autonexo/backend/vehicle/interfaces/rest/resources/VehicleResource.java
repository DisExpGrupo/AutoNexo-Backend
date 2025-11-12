package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import java.util.List;

/**
 * Resource for vehicle representation.
 */
public record VehicleResource(
    Long id,
    Long brandId,
    String model,
    Integer year,
    String licensePlate,
    String vin,
    String color,
    Integer currentMileage,
    List<String> imageUrls,
    boolean active,
    Long primaryOwnerId
) {}

