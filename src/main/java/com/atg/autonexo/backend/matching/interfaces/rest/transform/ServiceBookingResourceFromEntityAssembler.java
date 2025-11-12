package com.atg.autonexo.backend.matching.interfaces.rest.transform;

import java.util.List;
import java.util.stream.Collectors;

import com.atg.autonexo.backend.matching.domain.model.aggregates.ServiceBooking;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.ServiceBookingResource;

/**
 * Assembler for converting ServiceBooking entities to REST resources.
 */
public class ServiceBookingResourceFromEntityAssembler {
    
    public static ServiceBookingResource toResourceFromEntity(ServiceBooking entity) {
        List<String> servicesToPerform = entity.getServicesToPerform().stream()
            .map(Enum::name)
            .collect(Collectors.toList());
        
        return new ServiceBookingResource(
            entity.getId(),
            entity.getServiceRequestId(),
            entity.getOfferId(),
            entity.getUserId().id(),
            entity.getVehicleId(),
            entity.getWorkshopId().id(),
            entity.getScheduledDate(),
            entity.getProposedPrice() != null ? entity.getProposedPrice().amount() : null,
            entity.getProposedPrice() != null ? entity.getProposedPrice().currency() : null,
            entity.getFinalPrice() != null ? entity.getFinalPrice().amount() : null,
            entity.getFinalPrice() != null ? entity.getFinalPrice().currency() : null,
            entity.getStatus().name(),
            servicesToPerform,
            entity.getDescription(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
            entity.getCompletedAt(),
            entity.getPickedUpAt(),
            entity.getCancelledAt(),
            entity.getCancelledBy() != null ? entity.getCancelledBy().id() : null,
            entity.getCancellationReason()
        );
    }
}

