package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.util.List;

/**
 * Resource for workshop match result.
 */
public record WorkshopMatchResultResource(
    Long workshopId,
    String workshopName,
    Double distanceKm,
    Double rating,
    List<String> matchingServices,
    Double matchScore
) {}

