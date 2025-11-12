package com.atg.autonexo.backend.matching.interfaces.rest.transform;

import java.util.List;
import java.util.stream.Collectors;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceRequest;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.ServiceRequestResource;

/**
 * Assembler for converting ServiceRequest entities to REST resources.
 */
public class ServiceRequestResourceFromEntityAssembler {
    
    public static ServiceRequestResource toResourceFromEntity(ServiceRequest entity) {
        List<String> requestedServices = entity.getRequestedServices().stream()
            .map(Enum::name)
            .collect(Collectors.toList());
        
        return new ServiceRequestResource(
            entity.getId(),
            entity.getUserId().id(),
            entity.getVehicleId(),
            requestedServices,
            entity.getDescription(),
            entity.getUserLocation().latitude(),
            entity.getUserLocation().longitude(),
            entity.getSearchRadius().valueInKm(),
            entity.getStatus().name(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
            entity.getCancelledAt() != null ? entity.getCancelledAt() : null
        );
    }
}

