package com.atg.autonexo.backend.vehicle.domain.model.queries;

/**
 * Query to get all vehicles owned or authorized for a user.
 */
public record GetUserVehiclesQuery(
    Long userId
) {
    public GetUserVehiclesQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

