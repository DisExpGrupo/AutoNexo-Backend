package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resource for service request representation.
 */
public record ServiceRequestResource(
    Long id,
    Long userId,
    Long vehicleId,
    List<String> requestedServices,
    String description,
    Double latitude,
    Double longitude,
    Integer searchRadiusKm,
    String status,
    LocalDateTime createdAt,
    LocalDateTime cancelledAt
) {}

