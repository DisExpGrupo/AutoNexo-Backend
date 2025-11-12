package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/**
 * Resource for confirming schedule.
 */
public record ConfirmScheduleResource(
    @NotNull(message = "Scheduled date is required")
    LocalDateTime scheduledDate
) {}

