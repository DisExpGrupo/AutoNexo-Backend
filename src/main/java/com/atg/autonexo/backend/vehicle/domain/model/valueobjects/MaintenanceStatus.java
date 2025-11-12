package com.atg.autonexo.backend.vehicle.domain.model.valueobjects;

/**
 * Status of a maintenance record.
 * PENDING_CONFIRMATION: Created by workshop, waiting for user confirmation
 * CONFIRMED: Confirmed by user (or manual entry)
 * REJECTED: Rejected by user (workshop-created only)
 * MANUAL: Created manually by user (immediately confirmed)
 */
public enum MaintenanceStatus {
    PENDING_CONFIRMATION,
    CONFIRMED,
    REJECTED,
    MANUAL
}

