package com.atg.autonexo.backend.matching.domain.model.commands;

import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.matching.domain.model.valueobjects.SearchRadius;

/**
 * Command to create a new service request.
 */
public record CreateServiceRequestCommand(
    UserId userId,
    Long vehicleId,
    List<ServiceCatalog> requestedServices,
    String description,
    Coordinates userLocation,
    SearchRadius searchRadius
) {
    public CreateServiceRequestCommand {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (requestedServices == null || requestedServices.isEmpty()) {
            throw new IllegalArgumentException("RequestedServices cannot be null or empty");
        }
        if (userLocation == null) {
            throw new IllegalArgumentException("UserLocation cannot be null");
        }
        if (searchRadius == null) {
            throw new IllegalArgumentException("SearchRadius cannot be null");
        }
    }
}

