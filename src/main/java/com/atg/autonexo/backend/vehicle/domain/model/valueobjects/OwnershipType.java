package com.atg.autonexo.backend.vehicle.domain.model.valueobjects;

/**
 * Type of ownership for a vehicle.
 * PRIMARY: The main owner who has full control
 * AUTHORIZED: Authorized user (family member) who can view and request services
 */
public enum OwnershipType {
    PRIMARY,
    AUTHORIZED
}

